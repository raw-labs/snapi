/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.osr;

import com.esotericsoftware.kryo.io.Input;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.JoinComputeNext;
import raw.runtime.truffle.runtime.kryo.KryoNodes;
import raw.runtime.truffle.runtime.kryo.KryoNodesFactory;
import raw.runtime.truffle.tryable_nullable.TryableNullable;

public class OSRJoinNextNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  @Child
  FunctionExecuteNodes.FunctionExecuteTwo functionExecuteTwoNode =
      FunctionExecuteNodesFactory.FunctionExecuteTwoNodeGen.create();

  @Child KryoNodes.KryoReadNode kryoReadNode = KryoNodesFactory.KryoReadNodeGen.create();

  @CompilerDirectives.CompilationFinal private JoinComputeNext computeNext;

  private Object result = null;

  public Object getResult() {
    return result;
  }

  @CompilerDirectives.TruffleBoundary
  private Input createInput(File file, Node node) {
    try {
      return new Input(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e.getMessage(), e, node);
    }
  }

  public void init(JoinComputeNext computeNext) {
    this.computeNext = computeNext;
  }

  public boolean executeRepeating(VirtualFrame frame) {
    Object row = null;
    if (computeNext.getLeftRow() == null || computeNext.getRightRow() == null) {
      if (computeNext.getLeftRow() == null) {
        if (hasNextNode.execute(this, computeNext.getLeftGen())) {
          computeNext.setLeftRow(nextNode.execute(this, computeNext.getLeftGen()));
        } else {
          // end of left, nothing else to read
          throw new BreakException();
        }
      }
      if (computeNext.getKryoRight() == null) {
        computeNext.setKryoRight(createInput(computeNext.getDiskRight(), this));
        computeNext.setReadRight(0);
      }
      if (computeNext.getRightRow() == null) {
        if (computeNext.getReadRight() < computeNext.getSpilledRight()) {
          computeNext.setRightRow(
              kryoReadNode.execute(
                  this,
                  computeNext.getLanguage(),
                  computeNext.getKryoRight(),
                  computeNext.getRightRowType()));
          boolean pass;
          if (computeNext.getReshapeBeforePredicate()) {
            row =
                functionExecuteTwoNode.execute(
                    this,
                    computeNext.getRemap(),
                    computeNext.getLeftRow(),
                    computeNext.getRightRow());
            pass =
                TryableNullable.handlePredicate(
                    functionExecuteOneNode.execute(this, computeNext.getPredicate(), row), false);
            if (!pass) row = null;
          } else {
            pass =
                TryableNullable.handlePredicate(
                    functionExecuteTwoNode.execute(
                        this,
                        computeNext.getPredicate(),
                        computeNext.getLeftRow(),
                        computeNext.getRightRow()),
                    false);
            if (pass)
              row =
                  functionExecuteTwoNode.execute(
                      this,
                      computeNext.getRemap(),
                      computeNext.getLeftRow(),
                      computeNext.getRightRow());
          }

          computeNext.setReadRight(computeNext.getReadRight() + 1);
          computeNext.setRightRow(null);
        } else {
          // end of right, reset currentLeft to make sure we try another round
          computeNext.setLeftRow(null);
          computeNext.getKryoRight().close();
          computeNext.setRightRow(null);
          computeNext.setKryoRight(null);
        }
      }
    }
    if (row != null) {
      result = row;
      return false;
    }
    return true;
  }
}

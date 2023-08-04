package raw.runtime.truffle.ast.io.xml;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonReaderRawTruffleException;

import javax.xml.stream.XMLStreamReader;

@NodeInfo(shortName = "ParseXml")
public class XmlParseNode extends ExpressionNode {
  @Child private ExpressionNode strExp;

  @Child private DirectCallNode childDirectCall;

  @Child
  private XmlParserNodes.InitXmlParserNode initParserNode =
      XmlParserNodesFactory.InitXmlParserNodeGen.create();

  @Child
  private XmlParserNodes.CloseXmlParserNode closeParserNode =
      XmlParserNodesFactory.CloseXmlParserNodeGen.create();

  @Child
  private XmlParserNodes.SkipOneXmlParserNode skipOneNode =
      XmlParserNodesFactory.SkipOneXmlParserNodeGen.create();

  private XMLStreamReader xmlStreamReader;

  public XmlParseNode(ExpressionNode strExp, RootNode readerNode) {
    this.strExp = strExp;
    this.childDirectCall = DirectCallNode.create(readerNode.getCallTarget());
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    try {
      String str = (String) strExp.executeGeneric(virtualFrame);
      xmlStreamReader = initParserNode.execute(str);
      skipOneNode.execute(xmlStreamReader);
      return childDirectCall.call(xmlStreamReader);
    } catch (RawTruffleRuntimeException e) {
      throw new JsonReaderRawTruffleException();
    } finally {
      closeParserNode.execute(xmlStreamReader);
      xmlStreamReader = null;
    }
  }
}

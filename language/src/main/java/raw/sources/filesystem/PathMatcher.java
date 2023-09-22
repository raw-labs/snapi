///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.sources.filesystem;
//
//public interface PathMatcher {
//
//  /**
//   * Does the given <code>path</code> represent a pattern that can be matched by an implementation
//   * of this interface?
//   *
//   * <p>If the return value is <code>false</code>, then the {@link #match} method does not have to
//   * be used because direct equality comparisons on the static path Strings will lead to the same
//   * result.
//   *
//   * @param path the path String to check
//   * @return <code>true</code> if the given <code>path</code> represents a pattern
//   */
//  boolean isPattern(String path);
//
//  /**
//   * Match the given <code>path</code> against the given <code>pattern</code>, according to this
//   * PathMatcher's matching strategy.
//   *
//   * @param pattern the pattern to match against
//   * @param path the path String to test
//   * @return <code>true</code> if the supplied <code>path</code> matched, <code>false</code> if it
//   *     didn't
//   */
//  boolean match(String pattern, String path);
//
//  /**
//   * Match the given <code>path</code> against the corresponding part of the given <code>pattern
//   * </code>, according to this PathMatcher's matching strategy.
//   *
//   * <p>Determines whether the pattern at least matches as far as the given base path goes, assuming
//   * that a full path may then match as well.
//   *
//   * @param pattern the pattern to match against
//   * @param path the path String to test
//   * @return <code>true</code> if the supplied <code>path</code> matched, <code>false</code> if it
//   *     didn't
//   */
//  boolean matchStart(String pattern, String path);
//
//  /**
//   * Given a pattern and a full path, determine the pattern-mapped part.
//   *
//   * <p>This method is supposed to find out which part of the path is matched dynamically through an
//   * actual pattern, that is, it strips off a statically defined leading path from the given full
//   * path, returning only the actually pattern-matched part of the path.
//   *
//   * <p>For example: For "myroot/*.html" as pattern and "myroot/myfile.html" as full path, this
//   * method should return "myfile.html". The detailed determination rules are specified to this
//   * PathMatcher's matching strategy.
//   *
//   * <p>A simple implementation may return the given full path as-is in case of an actual pattern,
//   * and the empty String in case of the pattern not containing any dynamic parts (i.e. the <code>
//   * pattern</code> parameter being a static path that wouldn't qualify as an actual pattern). See
//   * {@link #isPattern pattern} for details on what constitutes a pattern. A sophisticated
//   * implementation will differentiate between the static parts and the dynamic parts of the given
//   * path pattern.
//   *
//   * @param pattern the path pattern
//   * @param path the full path to introspect
//   * @return the pattern-mapped part of the given <code>path</code> (never <code>null</code>)
//   */
//  String extractPathWithinPattern(String pattern, String path);
//}

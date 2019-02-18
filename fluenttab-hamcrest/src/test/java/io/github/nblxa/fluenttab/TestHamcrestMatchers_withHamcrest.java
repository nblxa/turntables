package io.github.nblxa.fluenttab;

public class TestHamcrestMatchers_withHamcrest {
/*
    @Test
    public void test_matcherIsNotEqual() {
        Tab actual = ActualExpected.tab_2x2();
        Tab expected = ActualExpected.expected_2x2_assertions();

        org.hamcrest.MatcherAssert.assertThat(actual, org.hamcrest.Matchers.not(
            org.hamcrest.Matchers.equalTo(expected)));
    }

    @Test
    public void test_matcherIsEqual_assertionError() {
        Tab actual = ActualExpected.tab_2x2();
        Tab expected = ActualExpected.expected_2x2_assertions();

        Throwable t = catchThrowable(
            () -> org.hamcrest.MatcherAssert.assertThat(actual,
                org.hamcrest.Matchers.equalTo(expected)));
        String message = new StringBuilder()
            .append("\n")
            .append("Expected: <Table:\n")
            .append("  -\n")
            .append("    - 1\n")
            .append("    - \"org.hamcrest.core.IsAnything\": \"ANYTHING\"\n")
            .append("  -\n")
            .append("    - \"org.hamcrest.core.IsInstanceOf\": \"an instance of java.lang.Integer\"\n")
            .append("    - 4>\n")
            .append("     but: was <Table:\n")
            .append("  -\n")
            .append("    - 1\n")
            .append("    - 2\n")
            .append("  -\n")
            .append("    - 3\n")
            .append("    - 4>")
            .toString();
        assertThat(t).hasMessage(message);
    }

    @Test
    public void test_matcherMatchesTab() {
        Tab actual = ActualExpected.tab_2x2();
        Tab expected = ActualExpected.expected_2x2_assertions();

        org.hamcrest.MatcherAssert.assertThat(actual, matchesTab(expected));
    }

    @Test
    public void test_matcherReverseMatchesTab_throwsException() {
        Tab actual = ActualExpected.tab_2x2();
        Tab expected = ActualExpected.expected_2x2_assertions();

        Throwable t = catchThrowable(
            () -> org.hamcrest.MatcherAssert.assertThat(expected, matchesTab(actual)));
        assertThat(t).isInstanceOf(AssertionEvaluationException.class);
    }

    @Test
    @Ignore
    public void test_matcherIsEqual_manualInIDE() {
        Tab actual = ActualExpected.tab_2x2();
        Tab expected = ActualExpected.expected_2x2_assertions();

        org.hamcrest.MatcherAssert.assertThat(actual, org.hamcrest.Matchers.equalTo(expected));
    }

    @Test
    public void test_matchesTab_withMismatch() {
        Tab actual = ActualExpected.actual_2x2_doesnt_match();
        Tab expected = ActualExpected.expected_2x2_assertions();

        Throwable t = catchThrowable(
            () -> org.hamcrest.MatcherAssert.assertThat(actual, matchesTab(expected)));
        String message = new StringBuilder()
            .append("\n")
            .append("Expected: <Table:\n")
            .append("  -\n")
            .append("    - 1\n")
            .append("    - 2\n")
            .append("  -\n")
            .append("    - \"org.hamcrest.core.IsInstanceOf\": \"an instance of java.lang.Integer\"\n")
            .append("    - 4>\n")
            .append("     but: was <Table:\n")
            .append("  -\n")
            .append("    - 1\n")
            .append("    - 2\n")
            .append("  -\n")
            .append("    - null\n")
            .append("    - 4>")
            .toString();
        assertThat(t).hasMessage(message);
    }

    @Test
    @Ignore
    public void test_matchesTab_withMismatch_manualInIDE() {
        Tab actual = ActualExpected.actual_2x2_doesnt_match();
        Tab expected = ActualExpected.expected_2x2_assertions();

        org.hamcrest.MatcherAssert.assertThat(actual, matchesTab(expected));
    }
 */
}

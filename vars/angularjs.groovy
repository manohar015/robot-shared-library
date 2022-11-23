def call() {
    node {
        git branch: 'main', url: "https://github.com/manohar015/${COMPONENT}.git"
        env.APPTYPE="angularjs"
        common.sonarCheck()
        common.lintCheck()
        env.ARGS="-Dsonar.sources=."
        common.testCases()
        if (env.TAG_NAME != null) {
            common.artifact()
        }
    }
}
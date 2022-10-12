def call() {
    node {
        git branch: 'main', url: "https://github.com/b50-clouddevops/${COMPONENT}.git"
        env.APPTYPE="angularjs"
        common.lintCheck()
        env.ARGS="-Dsonar.sources=."
        common.sonarCheck()
        common.testCases()
        if (env.TAG_NAME != null) {
            common.artifact()
        }
    }
}
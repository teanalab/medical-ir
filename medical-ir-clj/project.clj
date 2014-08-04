(defproject medical-ir-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [metamap-api "1.0-SNAPSHOT"]
                 [korma "0.3.2"]
                 [mysql/mysql-connector-java "5.1.31"]
                 [org.clojure/data.zip "0.1.1"]
                 [log4j "1.2.15" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]]
  :main ^:skip-aot medical-ir-clj.topics
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

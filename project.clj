(defproject pkrsim "0.0.1-SNAPSHOT"
  :description "Simulates poker hands (Texas Holdem Hands). Results can be used to calculate static information about the hands. The lib can also be used für poker hand Evaluation."
  :url "https://github.com/sradevel/pkrsim"
  :scm {:name "git"
        :url "https://github.com/sradevel/pkrsim"}
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/mit-license.php"}
  :signing {:gpg-key "sradevel@gmx.de"}
  :deploy-repositories [["clojars" {:creds :gpg}]]
  :plugins [[cider/cider-nrepl "0.8.2"]
                  [lein-autoexpect "1.4.0"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [expectations "2.0.13"]])

;; from https://gist.github.com/Chouser/5796967

(ns ags1.externs-for-cljs
  (:require [clojure.java.io :as io]
            [cljs.compiler :as comp]
            [cljs.analyzer :as ana]))

(defn read-file [file]
  (let [eof (Object.)]
    (with-open [stream (clojure.lang.LineNumberingPushbackReader. (io/reader file))]
      (vec (take-while #(not= % eof)
                       (repeatedly #(read stream false eof)))))))

(defn file-analysis [file]
  (binding [ana/*cljs-ns* 'cljs.user
            ana/*cljs-file* file]
    (mapv #(ana/analyze (ana/empty-env) %) (read-file file))))

(defn flat-file-analysis [file]
  (mapcat #(tree-seq :children :children %)
          (file-analysis file)))

(defn get-vars-used [ffa]
  (->> ffa
       (filter #(and (= (:op %) :var) (-> % :info :ns)))
       (map #(-> % :info :name))
       distinct))

(defn var-defined? [sym]
  (contains? (:defs (get @ana/namespaces (symbol (namespace sym))))
             (symbol (name sym))))

(defn get-undefined-vars [ffa]
  (remove var-defined? (get-vars-used ffa)))

(defn externs-for-var [sym]
  (if (= "js" (namespace sym))
    (format "var %s={};\n" (name sym))
    (format "var %s={};\n%s.%s=function(){};\n"
            (namespace sym) (namespace sym) (name sym))))

(defn get-interop-used [ffa]
  (->> ffa
       (filter #(and (= (:op %) :dot)))
       (map #(or (:method %) (:field %)))
       distinct))

(defn externs-for-interop [sym]
  (format "DummyExternClass.%s=function(){};\n" sym))

(defn externs-for-cljs [file]
  (swap! ana/namespaces empty)
  (ana/analyze-file "cljs/core.cljs")
  (let [ffa (flat-file-analysis file)]
    (apply str
           (concat
            (map externs-for-var (get-undefined-vars ffa))
            ["var DummyExternClass={};\n"]
            (map externs-for-interop (get-interop-used ffa))))))

;; (externs-for-cljs "/home/chouser/proj/cljs-example/src/main.cljs")

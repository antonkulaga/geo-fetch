import ammonite.ops._
//import $ivy.`org.phenoscape::scowl:1.3`

import $ivy.`org.eclipse.rdf4j:rdf4j-rio-rdfxml:2.4.2`
import $ivy.`org.eclipse.rdf4j:rdf4j-repository-sail:2.4.2`
import $ivy.`org.eclipse.rdf4j:rdf4j-sail-memory:2.4.2`
import $ivy.`org.eclipse.rdf4j:rdf4j-sail-nativerdf:2.4.2`

import $ivy.`com.github.pathikrit::better-files:3.7.0`
@

import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.RepositoryConnection
import org.eclipse.rdf4j.rio.RDFFormat
import java.io.File
import java.net.URL
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore

val repo = new SailRepository(new MemoryStore())
repo.initialize()
val file = new File("/data/indexes/GO/go-plus.owl")
val baseURI = "http://purl.obolibrary.org/obo/go.owl"
val con = repo.getConnection()
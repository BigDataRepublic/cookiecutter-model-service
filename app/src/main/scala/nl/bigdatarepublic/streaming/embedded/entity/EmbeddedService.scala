package nl.bigdatarepublic.streaming.embedded.entity

/**
  * Interface for all embedded services.
  */
trait EmbeddedService {

  def start()
  def stop()

}

package com.basselop.nickesmeck
import java.util.List;

import retrofit2.{Response, Callback, Call, Retrofit}
;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.converter.gson.GsonConverterFactory

import scala.concurrent.{Promise, Future}

case class Fighter(id: Int,
                   wins: Int,
                   statid: Int,
                   losses: Int,
                   last_name: String,
                   weight_class: String,
                   title_holder: Boolean,
                   draws: Int,
                   fighter_status: String,
                   thumbnail: String)

trait FittUfc {
  @GET("/fighters")      def getFighters(): Call[List[Fighter]]
  @GET("/fighters/{id}") def getFighter(@Path("id") id: Int): Call[Fighter]
}


object Backend {
  val UFC = new Retrofit.Builder()
    .baseUrl("http://ufc-data-api.ufc.com/api/v1/us")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(classOf[FittUfc])

  def getFromBackend[T](f: UFC.type => Call[T]): Future[T] = {
    val p = Promise[T]()
    f(UFC).enqueue(new Callback[T] {
      override def onFailure(call: Call[T], t: Throwable): Unit = p.failure(t)
      override def onResponse(call: Call[T], response: Response[T]): Unit = p.success(response.body())
    })
    p.future
  }

}
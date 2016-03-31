package com.basselop.nickesmeck

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import scala.collection.JavaConverters._
import scala.concurrent.Future

/**
 * Created by arneball on 2016-03-31.
 */
class MainActivity extends AppCompatActivity {
  implicit class FutureToUiThreadable[T](future: Future[T]) {
    def onUi(f: T => Unit) = future.onSuccess { case t =>
      runOnUiThread(new Runnable {
        override def run(): Unit = f(t)
      })
    }
  }

  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    Backend.getFromBackend(_.getFighters()).onUi { fighters =>

      val asScala = fighters.asScala
      val manyWins = asScala.filter{ _.wins > 100 }
      val manyWinsName = manyWins.map{ _.last_name }

      Toast.makeText(this, fighters.toString, Toast.LENGTH_LONG).show()
    }
  }
}

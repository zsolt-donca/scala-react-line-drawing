package zsd

import scala.swing._
import java.awt.{Color, Point}
import scala.react.Domain
import scala.swing.event.{MouseDragged, MouseReleased, MousePressed}

object MyDomain extends Domain {
  val scheduler = new SwingScheduler()
  val engine = new Engine
}

import MyDomain._

object ScalaReactLineDrawing extends SimpleSwingApplication with Observing {

  override def main(args: Array[String]) {
    schedule { startup(args) }
    start() // starts the scala-react engine
  }

  override def top: Frame = new MainFrame() {
    contents = new FlowPanel() {
      val mouseDown = EventSource[Point]
      val mouseMove = EventSource[Point]
      val mouseUp = EventSource[Point]

      val mainProgramFlow = Reactor.loop {
        self =>
        // step 1
          val path = new Path(self await mouseDown)
          self.loopUntil(mouseUp) {
            // step 2
            val m = self awaitNext mouseMove
            path.lineTo(m)
            draw(path)
          }
          path.close() // step 3
          draw(path)
      }

      listenTo(mouse.clicks, mouse.moves)
      reactions += {
        case MousePressed(_, point, _, _, _) => mouseDown << point
        case MouseReleased(_, point, _, _, _) => mouseUp << point
        case MouseDragged(_, point, _) => mouseMove << point
      }

      class Path(var positions: Seq[Point]) {
        def this(pos: Point) = this(Seq(pos))

        def lineTo(pos: Point) {
          positions = positions :+ pos
        }

        def close() {
          positions = positions :+ positions.head
        }
      }

      var pathDrawn = new Path(new Point(0, 0))
      def draw(path: Path) {
        pathDrawn = path
        repaint()
      }

      override protected def paintComponent(g: swing.Graphics2D): Unit = {
        super.paintComponent(g)

        val xPoints = pathDrawn.positions.map(pos => pos.x).toArray
        val yPoints = pathDrawn.positions.map(pos => pos.y).toArray
        g.setColor(Color.BLACK)
        g.drawPolyline(xPoints, yPoints, pathDrawn.positions.length)
      }
    }
    preferredSize = new Dimension(400, 300)
    pack()
  }
}

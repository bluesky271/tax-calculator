package example

import framework.Framework._
import org.scalajs.dom.html.Input
import rx.Ctx.Owner
import rx._
import wiki.WIKI

import scalatags.JsDom.all._

object ScalaJSExample2 {

  object model {
    val moneyInputField = Var(0.0)
    val expencesInputField = Var(0.0)
    val tax = 0.80

    val ir35CheckBoxIn = Var(false)
    val marriageCheckBoxIn = Var(false)
    val pensionReliefCheckboxIn = Var(false)
    val maintenanceReliefeChecboxIn = Var(false)
    val charityReliefeChecboxIn = Var(false)

    val expensesInput = Var(0.0)

    val earningAfterIncomeTax = Rx {
      moneyInputField() * tax
    }

    val earningAfterTaxDeductions = Rx {
      if (ir35CheckBoxIn()) {
        moneyInputField() * tax * 0.8
      } else {
        moneyInputField() * tax
      }

    }

    val stage1 = Rx {
      //some computations
      var taxAfterI = moneyInputField() * 0.8
      if (ir35CheckBoxIn()) {
        moneyInputField() * 0.8 * 0.8
      } else {
        moneyInputField() * 0.8
      }
    }

    val stage2 = Rx {
      if (marriageCheckBoxIn()) {
        stage1() + 231.0
      } else stage1()
    }

    val stage3 = Rx {
      if(pensionReliefCheckboxIn()) {
        stage2() * 1.1
      }
      else stage2()
    }

    val stage4 = Rx {
      if(maintenanceReliefeChecboxIn()) {
        stage3() + 320
      }
      else stage3()
    }

    val stage5 = Rx {
      if(charityReliefeChecboxIn()) {
        stage4() + 68
      }
      else stage4()
    }


    val takeHomeMoney =  Rx {
      stage5() + expencesInputField()
    }

  }

  object view {

    val ir35InputField: Input = {
      val i = input(`type` := "checkbox").render
      i.onchange = (x: Any) => model.ir35CheckBoxIn() = i.checked
      i
    }

    val marriageInputField: Input = {
      val i = input(`type` := "checkbox").render
      i.onchange = (x: Any) => model.marriageCheckBoxIn() = i.checked
      i
    }

    val pensionReliefInputField: Input = {
      val i = input(`type` := "checkbox").render
      i.onchange = (x: Any) => model.pensionReliefCheckboxIn() = i.checked
      i
    }

    val maintenanceInputField: Input = {
      val i = input(`type` := "checkbox").render
      i.onchange = (x: Any) => model.maintenanceReliefeChecboxIn() = i.checked
      i
    }

    val charityReliefInputField: Input = {
      val i = input(`type` := "checkbox").render
      i.onchange = (x: Any) => model.charityReliefeChecboxIn() = i.checked
      i
    }
    val expensivesInputField: Input = {
      val i = input(placeholder := "put money here").render
      i.onkeyup = (x: Any) => model.expencesInputField() = i.value.toDouble
      i
    }

    val calc = div(
      h1("Tax Calculator"),
      p(
        cls := "group",
        h3("Income/Earnings"),
        {
          val i = input(placeholder := "put money here").render
          i.onkeyup = (x: Any) => model.moneyInputField() = i.value.toDouble
          i
        }
      ),
      p("Earnings after income tax deductions and NI"),
      WIKI.main("Income Tax", label(WIKI.infoItem("Income Tax", WIKI.wikiservice.keys.IR35), input(readonly, value := model.earningAfterIncomeTax, cls := "output")).render),
      p("put some more info about your profile:"),
      span(
        style := "display: inline",
        WIKI.main("IR35", label(WIKI.infoItem("IR35", WIKI.wikiservice.keys.IR35), ir35InputField).render),
        WIKI.main("Marriage allowance", label(WIKI.infoItem("Marriage allowance", WIKI.wikiservice.keys.marriageAllowance), marriageInputField).render),
        WIKI.main("Pension Relief", label(WIKI.infoItem("Pension Relief", WIKI.wikiservice.keys.pensionRelief), pensionReliefInputField).render),
        WIKI.main("Maintenance Relief", label(WIKI.infoItem("Maintenance relief", WIKI.wikiservice.keys.maintenanceRelief), maintenanceInputField).render),
        WIKI.main("Maintenance Relief", label(WIKI.infoItem("Charity relief", WIKI.wikiservice.keys.charityRelief), charityReliefInputField).render)
      ),
      div(
        //  p("you have to pay taxes:"),
        //  taxToPayOutput,
        span(
          style := "display: inline",
          p(
            "Self-Employed/Contractor expenses amount", expensivesInputField
          ),
          p(
            "Earnings After Tax Deductions",
            input(readonly, value := model.takeHomeMoney, cls := "output")
          ),

          div(
            cls := "debug-pane",
            hidden, //comment it to hide debug
            p("ir35 = ", model.ir35CheckBoxIn.map(_.toString())),
            p("married = ", model.marriageCheckBoxIn.map(_.toString())),
            p("pension = ", model.pensionReliefCheckboxIn.map(_.toString())),
            p("maintenanceRelief = ", model.maintenanceReliefeChecboxIn.map(_.toString()))
          )
        )
      )
    )

  }

  val main = view.calc.render

  implicit def ctx: Owner = Ctx.Owner.safe()

}
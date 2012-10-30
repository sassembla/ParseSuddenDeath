package com.kissaki.parseSuddenDeath

import scala.util.parsing.combinator.RegexParsers
import scala.io.Source


/*

＿人人 人人＿
＞ 突然の死 ＜
￣Y^Y^Y^Y￣って書いてあるtxt を読ませると、、、

*/
object ParseSuddenDeath {
	def main(args:Array[String]) : Unit = {

		val targetFilePath = args(0)

		val source = scala.io.Source.fromFile(targetFilePath)
		val lines = source .mkString.replaceAll("\n", "+")
		source.close

		val parser = new ParseSuddenDeath

		try {
			parser.parseSuddenDeath(lines)

			}catch {
				case e:Exception => println("e	"+e)
				case other: Throwable => println("other	"+other)
			}
	}
}


trait SuddenDeathAST

case class Griff(word : String) extends SuddenDeathAST

case class Body (word : String)  extends SuddenDeathAST

case class Header() extends SuddenDeathAST
case class Footer() extends SuddenDeathAST
case class LeftAndBodyAndRight(griff:Griff) extends SuddenDeathAST
case class LeftAndBodyAndRights(griffs:List[Griff]) extends SuddenDeathAST
case class Lefter()  extends SuddenDeathAST
case class Righter()  extends SuddenDeathAST


case class WholeSuddenDeath(body:String)


class ParseSuddenDeath extends RegexParsers {
	/**
		パースする
	*/
	def parseSuddenDeath(input:String) = {
		val result = parseAll(parse, input).get
		
		println("result	"+result)


		result
	}


	/**
	 * 最小単位の"言葉"
	 */
	def griff : Parser[Griff] = """[＞＜人Y^]*""".r ^^ {
		case value => Griff(value)
	}

	/**
		ヘッダ(って何)
		上のアレの繰り返し
		ときおりスペースが入ったりする
	*/
	def head : Parser[Header] = "＿" ~ rep("人") ~ "＿" ^^ {default =>
		println("default	"+default)
		default match {
			case _ => Header()
		}
	}
	

	/**
		フッタ(って何)
		下のアレの繰り返し
	*/
	def foot : Parser[Footer] = "￣" ~ rep("Y^Y") ~ "￣" ^^ {default =>
		default match {
			case _ => Footer()
		}
	}


	/**
		レフター(って何)
		左のアレ
	*/
	def left : Parser [Lefter] = "＞" ^^ {default =>
		default match {
			case _ => Lefter()
		}
	}

	/**
		ボディ
		突然の死
	*/
	def body : Parser [Griff] = griff ^^ {default =>
		default match {
			case _ => Griff("s")
		}
	}

	/**
		ライター(って何)
		右のアレ
	*/
	def right : Parser [Righter] = "＜" ^^ {default =>
		default match {
			case _ => Righter()
		}
	}

	def leftAndBodyAndRight :Parser[LeftAndBodyAndRight]  = left ~ body ~ right ^^ {default =>
		default match {
			case _ => {
				val g = new Griff("s")
				LeftAndBodyAndRight(g)
			}
		}
		

	}

	/**
		突然の死　の真ん中列をパースする。
		
		ヘッダ
		左　文字　右
		フッタ

		になるので、まんなか列が縦にふくれあがる可能性を考えたり考えなかったり
	*/
	def leftBodyRightRep :Parser[LeftAndBodyAndRights] = rep(leftAndBodyAndRight) ^^ {default =>
		println("LeftAndBodyAndRights default	"+default)
		default match {
			case _ => {
				val griffs :List[Griff] = List(new Griff("a"))
				LeftAndBodyAndRights(griffs)
			}
		}
	}

	/**
		ヘッダ
		
		(左　文字　右)　このへんは繰り返しになるんだろうきっと
		
		フッタ
	*/
	def parse : Parser[WholeSuddenDeath] = head ~ "+" ~ rep(leftBodyRightRep ~ "+") ~ foot ^^ { default =>
		println("WholeSuddenDeath default	"+default)
		default match {
			/*
			leftBodyRightRepからbodyだけを抜き出して、返す
			*/
			case _ => WholeSuddenDeath("まだてきとう")
		}
		
	}

}
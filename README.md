*この死霊は途中です

#**Scalaでのパーサコンビネータについて**

例として、突然の死


＿人人人人人＿  
＞ 突然の死 ＜  
￣Y^Y^Y^Y￣

を、パースして中の文章を切り出すパーサを作ってみます。
***
####出来上がると

＿人人人人人＿  
＞ 突然の死 ＜  
￣Y^Y^Y^Y￣  
から、

####突然の死

という文字が抽出され、Mapに入ります。   
Mapのkeyは、wordか何かにしておきましょう。  

*Map( word -> 突然の死 )*

 
**・パースすると、特定の条件を満たした文字列が、特定の形のオブジェクトになる！  **  
という感じです。  

***
パースには、scala.util.parsing.combinator.RegexParsers を使います。


```
import scala.util.parsing.combinator.RegexParsers
```

まず、文字列を分解する過程で、どのパターンがどうなるのか、というのを、  
case classを使って記述します。

今回はこんな感じ。  

```  
trait SuddenDeathAST

case class Griff(griff : String) extends SuddenDeathAST

case class Body (lineOfBody:String)  extends SuddenDeathAST

case class Header() extends SuddenDeathAST
case class Footer() extends SuddenDeathAST
case class LeftAndBodyAndRight(griff:Griff) extends SuddenDeathAST
case class LeftAndBodyAndRights(griffs:List[Griff]) extends SuddenDeathAST
case class Lefter()  extends SuddenDeathAST
case class Righter()  extends SuddenDeathAST

case class WholeSuddenDeath(body:String)  
```

まずASTをtraitとして宣言しておいて、各case classで継承してます。 
 
各case classは、パーサでのパターンマッチに使います。

たとえばHeaderとかは、

```
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
```  
＿人人人人人＿  
＞ 突然の死 ＜  
￣Y^Y^Y^Y￣  
の上の方の部分をアレするパーサmethodなのですが、 
 
このheadを使用すると、**"_"** で始まって **"人"** を繰り返して **"_"** で終わるパーツが   
一発で検出できます。  
**rep**　は、繰り返しを表す RegexParsers の関数です。

で、  

これらのパーサを、関数として組み合わせることができます。  

```  
	/**
		ヘッダ
		
		(左　文字　右)　このへんは繰り返しになるんだろうきっと
		
		フッタ
	 */
	def parse : Parser[WholeSuddenDeath] = 
	 head ~ "+" ~ rep(leftBodyRightRep ~ "+") ~ foot ^^ { default =>
		println("WholeSuddenDeath default	"+default)
		default match {
			case _ => WholeSuddenDeath("まだてきとう")
		}
		
	}
```

これで、 

####**head** が最初にあって、+記号があって、  
####次に **leftBodyRightRep** というパーツがあって、  
####その後ろにまた+記号があって、   
####最後に **foot** がついている  

_  
という、組み合わせを表現しています。  
真ん中にrepがあるのは、複数行にわたって**濃密な死**が記述されてる  
ステキな可能性を考慮しての物です。  


で、ほんとはコレで一つ言語を作ってて、死霊もそれ用のがあったのですが、  
ちょっと公開しちゃだめっぽいので、  
これでお茶を濁させて下しあ、、  

##＿人人人人人人＿  
##＞ 突然の死！！＜  
##￣Y^Y^Y^Y^Y^￣  














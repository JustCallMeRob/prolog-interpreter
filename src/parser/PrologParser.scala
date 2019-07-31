package parser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

trait Data
case class Database(listOfData : List[Data])
case class Fact(head: Atom, body: List[Atom]) extends Data
case class Rule(rule : Fact, predicate : Fact, predicateList : List[Fact]) extends Data
case class Atom(a : String)

class PrologParser extends StandardTokenParsers {

  lexical.delimiters += (":-", ",", "(", ")", ".")


  def parseAll[T](p: Parser[T], in: String): ParseResult[T] = {
    phrase(p)(new lexical.Scanner(in))
  }

  def database: Parser[Database] = rep(data) ^^ {
    case a => {
      println("Found file")
      new Database(a)
    }
  }

  def data: Parser[Data] = (rule | fact) <~ "." ^^ {
    case a => a
  }

  def fact: Parser[Fact] = (atom <~ "(") ~ atom ~ opt(rep("," ~> atom)) <~ ")" ^^ {
    case a ~ b ~ None => {
      val c = List(b)
      new Fact(a, c)
    }

    case a ~ b ~ c => {
      val d = b :: c.get
      new Fact(a, d)
    }
  }

  def rule: Parser[Rule] = (fact <~ ":-") ~ fact ~ rep("," ~> fact) ^^ {
    case a ~ b ~ c => {
      new Rule(a, b, c)
    }
  }

  def atom: Parser[Atom] = ident ^^ {
    new Atom(_)
  }

}

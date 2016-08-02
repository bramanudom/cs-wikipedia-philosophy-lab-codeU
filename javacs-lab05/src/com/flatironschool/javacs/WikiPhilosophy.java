package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	static	ArrayList urlsVisited = new ArrayList();
	final static String finalUrl = "https://en.wikipedia.org/wiki/Philosophy";
	static boolean finishedCrawling = false;

	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {


		WikiPhilosophy test = new WikiPhilosophy();
		test.crawl("https://en.wikipedia.org/wiki/Java_(programming_language)");
		

	}

	/* crawl: provides actual functionality
		@params: a String that represents the url at which to start crawling from
		@return: void, but will print out a list of urls visited in the console
	*/

	public static void crawl (String url){
		String currentLink = url;

		while(!finishedCrawling){

			try{

				if(findFirstLink(currentLink).equals(finalUrl)){
					urlsVisited.add(currentLink);
					finishedCrawling = true;
				}

			urlsVisited.add(currentLink);
			//System.out.println(currentLink);
			currentLink = findFirstLink(currentLink);
			//System.out.println(currentLink);
			}

			catch (IOException e){
				System.out.print("There was an error crawling");

			}

			catch (Exception e){
				System.out.print(e);
			}
	
		}

		System.out.print(urlsVisited);	

	}

	/* findFirstLink: helper method
		@params: takes in a String that represents the URL of the page to search through
		@return: the String representation of the first link found on the page 
		Throws an exception if there are issues crawling or if the page has 
		no (valid) links
	*/
	public static String findFirstLink (String url) throws IOException, Exception{
		Elements doc = wf.fetchWikipedia(url);
		Element  firstpara = doc.get(0);
		Iterable <Node> iter = new WikiNodeIterable(firstpara);
		int numRParen = 0;
		int numLParen = 0;
		boolean foundParen = false;
		for (Node node: iter) {

			/*	if the node encountered is a textnode, we want to keep track of the 
				parenthesis - - all non textNodes that are encountered should be ignored 
				until all parenthesis are closed. 
			*/

			if (node instanceof TextNode){
				TextNode accesibleNode = (TextNode)node;
				char[] chars = accesibleNode.text().toCharArray();
					for (char c: chars){
						if (c == '('){
							// System.out.println("found a paren");
							// System.out.println(node);
							numRParen ++;
						}
						else if (c == ')') {
							numLParen++;
						}
					}
					
				}
			else if ((numLParen == numRParen) && node instanceof Element){
				Element accesibleNode = (Element)node;
				String tag = accesibleNode.tagName();
					if(tag.equals("a") && isValidLink(accesibleNode)){
					return accesibleNode.attr("abs:href");
					}
			}
		}
		throw new Exception("No links found");
	}

		


	/* isValidLink: helper method; takes in a Element object and an iterator
		checks to see if there aare i or em tages up the parent tree
		returns a boolean indicating whether or not the link is valid
	*/


	public static boolean isValidLink(Element link){
		Element parent = link.parent();
		String parentTag = parent.tagName();
		return !(parentTag.equals("i") || parentTag.equals("em"));
	
	}
}

muddler.Page.start(rm) {

	database "default"
	
	def pages = loadTable """
					select title,url
					from pages
					order by title
					"""

 	viewParam "pages", pages
	
	setView 'index.html'
}

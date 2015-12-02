import com.grachro.muddler.Tsv;

Tsv.start(rm)
{
	delegate = it
	database "mysql"

	load"""
			select *
			from Foo
			order by Bar
		"""

 	viewParam "list"
}

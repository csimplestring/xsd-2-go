xsd-2-go
========

Given a XSD (XML Schema Definition) file, generating corresponding Golang XML structs. 
This tool is written in Java.

1 Requirements
--- Java 1.6 or above.
--- XJC (Jaxb Binding Compiler) 

2 Usage
Using this tool is pretty easy: download jar folder, then go to jar folder.
In that folder, there are a jar file and run.sh script. You just need to give 
the path of xsd file:

run.sh test.xsd 

Then it will output 

type XMLVendor struct {
	XMLName xml.Name 	`xml:"vendor"`
	Value	[]string	`xml:"value"`
}

type XMLVersion struct {
	XMLName xml.Name 	`xml:"version"`
	Id	string			`xml:"id,attr"`
	Value	bool		`xml:"value,attr"`
}
.............



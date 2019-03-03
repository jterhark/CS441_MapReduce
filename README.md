#Homework 2 - Jake TerHark
##Run Hadoop Job
Using hdfs, place the ```data.csv``` file into an HDFS location with the correct permissions.
In the root directory of the project, run ```sbt assembly``` to build an Uber Jar titled ```CsvGrapher.jar```.
Copy that jar to a remote machine running Hadoop MapReduce2 (optional).
Run the command ```hadoop jar CsvGrapher.jar {path_to_data} {output_path}```, replacing the paths with your own.
I chose this route instead of hardcoding values in the config file to allow for bash autocomplete.
The output will be in csv format and can be imported into gephi.

##Testing
To run the tests that make sure the needed libraries are loaded, simply run ```sbt test``` in the project root.

##Methodology
###Preprocessing Data
Since there is no default XML Input Format for MapReduce, I chose to preproccess the xml file, rather than write my own formatter.
The two data files I started with were ```dblp-2019-02-01.xml``` which contained the data, and ```dblp-2017-08-29.dtd``` which contained the schema
From the ```dtd``` file I copied the unicode entity elements into the ```DOCTYPE``` element in the xml file for unicode support.
I then ran a simple .net script on the file to transform the data from xml into csv (see end for script).
If any article had these authors:
```xml
<author>Mark Grechanik</author>
<author>B. M. Mainul Hossain</author>
<author>Ugo Buy</author>
```

Then this data would be transformed to:
```csv
"Mark Grechanik","B. M. Mainul Hossain","Ugo Buy" <-newline here
```

###Mapping and Reducing
Due to the structure of the data transformation, I was able to use the Text format which shards on the newline character.
Thus, each value passed to the mapper would be the shared authors of one article.
The mapped values for authors ```a, b, c``` would be:

	
| Key | Value |

| ---|---|

| a | b |
| a | c |
| b | c |


###Graph Visualization

###C# Transform Script
```C#
using System.IO;
using System.Linq;
using System.Xml.Linq;

namespace ConsoleApp1
{
    class Program
    {
        static void Main(string[] args)
        {
            var xml = XElement.Load(@"C:\Users\jaket\Downloads\dblp-2019-02-01.xml");
            var n = xml.FirstNode as XElement;
            using (var writer = new StreamWriter(@"C:\Users\jaket\Desktop\data.csv"))
            {
                while (n != null)
                {
                    var authors = n.Descendants("author").Select(x => $"\"{x.Value.Replace(",", "")}\"").ToArray();
                    writer.WriteLine(string.Join(',', authors));
                    n = n.NextNode as XElement;
                }
            }
        }
    }
}
```


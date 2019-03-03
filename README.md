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
<inproceedings>
    <author>Mark Grechanik</author>
    <author>B. M. Mainul Hossain</author>
    <author>Ugo Buy</author>
</inproceedings>
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
| --- | ----- |
| a   | b     |
| a   | c     |
| b   | c     |

Singular authors get mapped to themselves. The reason for this is explained in the graph visualization.
The reducer simply combines these key/value pairs in a csv file of the following format:
```csv
...
Sarmimala Saikia,Ashwin Srinivasan
Sarmin Hamidi,Mohammad Mahdi Rahimi
Sarmin Hossain,Laurence Brooks
Sarminah Samad,Azar Alizadeh
Sarminah Samad,Elnaz Akbari
Sarmishtha Ghoshal,Krishnendu Chakrabarty
Sarmishtha Ghoshal,Krishnendu Chakrabarty
Sarmishtha Ghoshal,Bhargab B. Bhattacharya
Sarmishtha Ghoshal,Krishnendu Chakrabarty
Sarmishtha Ghoshal,Bhargab B. Bhattacharya
Sarmishtha Ghoshal,Bhargab B. Bhattacharya
...
```

If multiple output files are produced, merge the files into one. (Just copy and past, nothing special needed.)

###Graph Visualization
Import the graph into Gephi as a mixed csv format with undirected edges as explained [here](https://gephi.org/users/supported-graph-formats/csv-format/).
Since repeats in the mapping/reducing process were kept, anytime another key/value pair that contain the same values is imported, the weight of the edge is increased by one.
In the overview tab, go to appearance->Nodes->Ranking, click the node size icon, choose the ranking to be degree, and click Apply.
This sets the size of the node to be the number of edges attached to the node.
This represents the number of articles that a particular author has written. This is why the self loops were kept.
Other areas for appearance include:

* Show edge weights
* Show node labels (authors)
* Run eigenvector statistic script and arrange via the results
Export the graph in whatever format you wish.

###C# Transform Script
```csharp
using System.IO;
using System.Linq;
using System.Xml.Linq;

namespace XmlTransform
{
    class Program
    {
        static void Main(string[] args)
        {
            var xml = XElement.Load(@"H:\data\dblp-2019-02-01.xml");
            var n = xml.FirstNode as XElement;
            using (var writer = new StreamWriter(@"H:\data\data.csv"))
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


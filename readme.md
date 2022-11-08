# Simple file indexer
Allows to:

- Index files of different formats and folders
- Monitor files and folders changes 
- Search in index, including composite and wildcard queries

Search examples:

- search Simple
- search Wild*rd
- search Composite Query
- search Composite Wi*rd Query

## Startup Parameters:
- **configurationPath**
  - Path to extenal file with configuration parameters
  - Default is empty 


- **stopWordsPath**
  - Path to file with stop words list 
  - Default is empty (Default list of stop words is used)

    
- **workerThreadsCount**
  - Amount of threads to process files in parallel
  - Default is 4


- **minTokenLength**
  - Minimum token length to be indexed (Including this value)
  - Default is 3


- **maxTokenLength**
  - Maximum token length to be indexed (Not including this value)
  - Default is 128


- **maxFileSize**
  - Maximum file size to be indexed in bytes
  - Default is 5242880 (5 MB)


- **processHiddenFiles**
  - Should index hidden files or not
  - Default is false


- **processFilesWithNoExtension**
  - Should index files without extension or not (File will be treated as a simple text file)
  - Default is false


- **processFilesWithUnknownExtension**
    - Should index files with unknown extension or not (File will be treated as a simple text file)
    - Default is false


- **watcher.enabled**
    - Is index watcher is enabled
    - Default is false
    

- **watcher.timeout**
    - Timeout between index watcher runs in msec
    - Default is 1000

## Available Commands:

- **cd** - Change path to specified directory
    - _path_ - Absolute or relative path to directory
- **dir** - List current directory
- **index** - Add files to the index 
  - -r - Index folders recursively; default is false
  - -d - Max recursive index depth; default is 10
  - _path_ - Absolute or relative path to add to index; can be file or directory; leave empty if you want to index current path
- **delete** - Remove files from the index
  - _path_ - Absolute or relative path to remove from index; can be file or directory; leave empty if you want to index current path
- **cleanup** - Cleanup the index memory
- **search** - Search in index
  - -s - Keep words order strict
  - _searchText_ - Text to search; can be multiple words; words can contain wildcards (*)
- **exit** - Finish all running processes and exit
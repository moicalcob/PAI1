import sys,os
try:
    import pandas as pd
except:
    os.system("pip3 install pandas")
    
root = "/Users/moises/Downloads" # it may have many subfolders and files inside
lst = []
from fnmatch import fnmatch
pattern = "*.*"        # Note: Use this pattern to get all types of files and folders 
for path, subdirs, files in os.walk(root):
    for name in files:
        if fnmatch(name, pattern):
            lst.append((os.path.join(path, name)))
df = pd.DataFrame({"filePaths":lst})
df.to_csv("filepaths.csv")
# Gitlet Design Document

**Name**: yangx

## Classes and Data Structures

## Algorithms

## Persistence
```
CWD                         <=== The current working directory.
└─── .gitlet
    ├─── logs               <=== Logs of each branch.
        ├─── master         <=== Logs of branch master.
        └─── xxx            <=== Other branch's logs.
    ├─── objects            <=== Blobs of files and commits.
    ├─── refs               <=== Information of HEADs.
        ├─── heads          <=== Local HEADs.
            ├─── master     <=== The default branch.
            └─── xxx        <=== Branch you've made.
    ├─── staging            <=== The staging area.
    └─── HEAD               <=== Where the HEAD point points at.
```


# 3.0.9

- V2 reader now checks the version header for validity
- added more testing for V2 reader

# 3.0.10

- fixed `DataDrivenConstantMapper` consuming the version header before parsing the definitions
  - this would cause a crash when using `DataDrivenConstantMapper` and V2 data!
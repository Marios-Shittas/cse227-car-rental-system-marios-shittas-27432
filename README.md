# Car Rental System

Semester project for `CSE227`.

This is a console-based Java application for managing a car rental company.  
It supports vehicles, customers, rentals, invoice printing, file storage, loyalty points, and maintenance tracking.

## How to run

Compile:

```bash
javac -d out src/*.java
```

Run:

```bash
java -cp out Main
```

## Main features

- Add and delete vehicles
- Register and search customers
- Create rentals
- Print and reprint invoices
- Show vehicle availability for a date or date range
- Show customer rental history
- Show vehicle history
- Print all stored data
- Loyalty programme
- Maintenance tracking

## Project structure

- `src/` contains the Java source files
- `data/` contains the text files used for storage
- `out/` contains compiled class files
- `report-site/` contains the small report website

## Notes

The program loads data automatically from the files in the `data/` folder when it starts.  
Changes such as new customers, vehicles, rentals, and maintenance records are saved immediately.

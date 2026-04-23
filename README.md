# Car Rental System

Semester project for `CSE227`.

This project is a console-based Java application for managing a car rental company.  
It supports vehicles, customers, rentals, invoice printing, file storage, loyalty points, and maintenance tracking.

## Online presentation

Project presentation / report website:

**https://marios-shittas.github.io/cse227-car-rental-system-marios-shittas-27432/**

This is the web presentation of the project, not the Java application itself.  
It was created as an online report/presentation page so the lecturer and other visitors can view the system overview, design, structure, and main functionality through a public URL.

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
- `docs/` contains the web presentation used for GitHub Pages
- `out/` contains compiled class files

## Notes

The program loads data automatically from the files in the `data/` folder when it starts.  
Changes such as new customers, vehicles, rentals, and maintenance records are saved immediately.

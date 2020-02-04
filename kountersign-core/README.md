# Kounter Sign Core

Core library consisting of tools that allow generation of complex passwords.

## Random Character Password Generator

Password generator at a character level. Generates password based on given characters and a specific length.

```kt
RandomCharacterPasswordGenerator().generate() // g:4aXe1`5&
```

## Random Pass Phrase Generator

Password generator at a string level. Generates password based on given words and a specific word count.
Also capable of injecting digits around words based on a given digit placement strategy.

Creates a passphrase with 2 uncapitalized words and injects 3 digits at the end of the phrase.
```kt
val someWordDictionary = ...
val strategy = DigitPlacementStrategy(
    // Default digit count is 3
    // Default uses all digit characters
    pattern = DigitPlacementPattern.END
)
RandomPassPhraseGenerator(
    words = someWordDictionary, 
    wordCount = 2, 
    digitPlacementStrategy = strategy
).generate() // computerdog413
```

Creates passphrase with 3 capitalized words and inject 1 digit between each word.
```kt
val someWordDictionary = ...
val strategy = DigitPlacementStrategy(
    digitCount = 1
    pattern = DigitPlacementPattern.BETWEEN_WORDS
)
RandomPassPhraseGenerator(
    words = someWordDictionary, 
    wordCount = 3, 
    capitalizeWords = true
    digitPlacementStrategy = strategy
).generate() // Grass7Car9Stomach
```

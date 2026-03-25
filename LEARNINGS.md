# Learning Reflection

## 1. What was the hardest bug?
The hardest challenge was ensuring the JavaFX TableView synchronized correctly with the underlying `ArrayList` in the `ContactManager`. By default, JavaFX Tables bind nicely to `ObservableList`, but our requirements stated we should focus on purely maintaining an in-memory `ArrayList`. When a record was updated or a vCard was imported, the TableView wouldn't reflect these changes automatically, creating a state mismatch between the UI and the data layer.

## 2. How did you fix it?
To resolve this, I implemented an observer-like pattern where the UI explicitly tells the TableView to `refresh()` after calling the `ContactManager`'s update/add/import functions. I also converted the core `ArrayList` into a JavaFX `FXCollections.observableArrayList()` wrapper specifically in the UI layer while keeping the core logic dependent on standard Java `List` objects inside `ContactManager`, bridging the gap between raw data structures and reactive UI components.

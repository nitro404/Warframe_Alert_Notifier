# Warframe Alert Notifier 1.1.2

## Description
A highly customizable alert notifier for Warframe.

## Notes
This project is abandoned and no longer developed or maintained. Does not function with the latest Warframe alerts API.

## Requirements
+ Java Runtime Environment (Version 1.6 or newer)

## How to Use
Simply open "Warframe Alert Notifier.jar" to run the program. All required configuration files will be generated at first launch. All settings are automatically saved after the program is closed.

There are 3 tabs in the main window to display all alerts, filtered alerts and a console with debug output and extra information.

Reward filters can be changed by using the filter editor (Filters > Edit Filters).

Navigate through the different categorized tabs to select which rewards to filter.

You can right-click to select / de-select / invert all rewards within an active tab.

When filters are updated after the editor is closed, they will not be written to configuration files until the user manually saves or closes the program.

A custom sound file will be played when a new alert matches any reward filter, and it will be added to the filtered alerts tab.

Reward and sound editor dialogs will soon be added to the program. Rewards and sounds can be manually updated using the configuration files, should you choose to do so.

## Changelog
+ Version 1.1.2 (April 28, 2014) (Indev Release)
  - Actually fixed "PHORID SPAWN" text appearing, it should no longer appear.
  - Fixed broken parsing for forma and reactor / catalyst blueprints on invasions.

+ Version 1.1.1 (April 24, 2014) (Indev Release)
  - Fixed "PHORID SPAWN" appearing in front of boss outbreak missions.
  - Fixed parsing to handle blueprints for reactors, catalysts and formas on outbreaks and invasions.

+ Version 1.1.0 (April 19, 2014) (Indev Release)
  - Updated to new XML feed link and format.
  - Added support for outbreaks and invasions.
  - Added entries for new auras and helmet blueprints.

+ Version 1.0.0 (October 17, 2013)
  - Initial release!

Feature: Categories

  Background: Users has categories
    Given "Alibaba" user with categories: food, house, my car
    And "Cassim" user with categories: food, bills, hobby
    And I am "Alibaba" user

  Scenario Outline: Add category
    When I add "<categoryName>" category
    Then I receive HTTP Created status
    And I have 4 categories: food, house, my car, <categoryName>

    Examples: 
    | categoryName      |
    | bills             |
    | holidays          |
    | my wife's clothes |

  Scenario: Add a category that already exists
    When I add "food" category
    Then I receive HTTP Conflict status
    And I have 3 categories: food, house, my car

  Scenario Outline: Edit category
    When I change name "<from>" account to "<to>"
    Then I receive HTTP OK status
    And I have 3 accounts: <to>, <category1>, <category2>

    Examples:
    | from      | to        | category1 | category2 |
    | food      | meal      | house     | my car    |
    | house     | my home   | food      | my car    |
    | my car    | car       | food      | house     |

  Scenario: Edit a category that does not exist
    When I change name "stone" category to "car"
    Then I receive HTTP Not Found status
    
  Scenario: Change a category name to that already exists
    When I change name "food" category to "house"
    Then I receive HTTP Conflict status
    
  Scenario Outline: Remove a category
    When I remove "<name>" category
    Then I receive HTTP OK status
    And I have 2 records: <category1>, <category2>

    Examples:
    | name      | category1 | category2 |
    | food      | house     | my car    |
    | house     | my car    | food      |
    | my car    | food      | house     |

  Scenario: Remove a category that does not exist
    When I remove "stone" account
    Then I receive HTTP Not Found status

  Scenario: Remove a category that has transactions
    Given a "bills" category with transactions
    When I remove "bills" category
    Then I receive HTTP Bad Request status
    And I have 4 categories: food, house, my car, bills

  Scenario: Remove a category that has a sub category
    Given a "food" category has categories
    When I remove "food" category
    Then I receive HTTP Bad Request status
    And I have 3 categories: food, house, my car
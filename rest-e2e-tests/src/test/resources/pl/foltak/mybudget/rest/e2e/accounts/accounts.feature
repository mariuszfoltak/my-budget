Feature: Accounts

  Background: An user account
    Given "Alibaba" user with accounts: bank, wallet, my socks
    And "Cassim" user with accounts: strongbox, wallet, placing
    And I am "Alibaba" user

  Scenario Outline: Add account
    When I add "<accountName>" account
    Then I receive HTTP Created status
    And I have 4 accounts: bank, wallet, my socks, <accountName>

    Examples: 
    | accountName       |
    | placing           |
    | strongbox         |
    | my wife's purse   |

  Scenario: Add account that already exists
    When I add "bank" account
    Then I receive HTTP Conflict status
    And I have 3 accounts: bank, wallet, my socks

  Scenario Outline: Edit account
    When I change name "<from>" account to "<to>"
    Then I receive HTTP OK status
    And I have 3 accounts: <to>, <account1>, <account2>

    Examples:
    | from      | to        | account1  | account2  |
    | wallet    | purse     | bank      | my socks  |
    | my socks  | purse     | bank      | wallet    |
    | wallet    | my wallet | bank      | my socks  |

  Scenario: Edit account that does not exist
    When I change name "stone" account to "purse"
    Then I receive HTTP Not Found status
    
  Scenario: Change account name to that already exists
    When I change name "bank" account to "wallet"
    Then I receive HTTP Conflict status
    
  Scenario Outline: Remove an account
    When I remove "<name>" account
    Then I receive HTTP OK status
    And I have 2 accounts: <account1>, <account2>

    Examples:
    | name      | account1  | account2  |
    | wallet    | bank      | my socks  |
    | my socks  | bank      | wallet    |
    | bank      | wallet      | my socks  |

  Scenario: Remove account that does not exist
    When I remove "stone" account
    Then I receive HTTP Not Found status

  Scenario: Remove account that has transactions
    Given an wallet account has transactions
    When I remove "wallet" account
    Then I receive HTTP Bad Request status
    And I have 3 accounts: bank, wallet, my socks
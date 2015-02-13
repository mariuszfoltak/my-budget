Feature: Authentication

  Background: An user account
    Given an "alibaba" user account with "fortythieves" password

  Scenario: Correct authentication
    When I send "alibaba" username
    And "fortythieves" password
    Then service logged me in

  Scenario: Without any authentication
    When I don't send any credentials
    Then service did not authorize me

  Scenario Outline: Wrong username or password
    When I send "<username>" username
    And "<password>" password
    Then service did not authorize me
    
  Examples: Wrong username
    | username | password       |
    | alladyn  | fortythieves   |
    | peterpan | fortythieves   |
    | alladyn  | fortythieves   |
    
  Examples: Wrong password
    | username | password       |
    | alibaba  | lamp           |
    | alibaba  | captainhook    |
    | alibaba  | captainhook    |
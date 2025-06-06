describe("Activity Manager App", () => {
  // Before each test, set up a stub for the initial GET /activities call.
  // Each test starts by navigating to the home page with predictable data to work with.
  beforeEach(() => {
    cy.intercept("GET", "http://localhost:8080/activities", {
      statusCode: 200,
      body: [
        { id: 1, name: "Test Activity 1", type: "Exercise", participants: 1 },
        { id: 2, name: "Test Activity 2", type: "Study", participants: 2 }
      ],
    }).as("getActivities");

    cy.visit("/");
    cy.wait("@getActivities");
  });

  // Verify that the table exists and contains two rows.
  // Check the contents of the first row.
  it("displays the list of activities", () => {
    cy.get("table").should("exist");
    cy.get("tbody > tr").should("have.length", 2);

    cy.get("tbody > tr")
      .first()
      .within(() => {
        cy.contains("Test Activity 1");
        cy.contains("Exercise");
        cy.contains("1");
      });
  });

  // Verify that the dialog appears when clicking on the "Add New Activity" button.
  // Check that the dialog contains the expected text fields and that they are available.
  it("opens the add activity dialog when 'Add New Activity' is clicked", () => {
    cy.contains("button", "Add New Activity").click();

    cy.get('[role="dialog"]').should("be.visible");

    cy.get('[role="dialog"]').within(() => {
      cy.contains("Create Activity");
      cy.get("input").should("have.length", 3);
    });
  });

  // Stub the POST call to create a new activity, and the subsequent call to refresh the data.
  // Open the dialog, fill in the form, and save the new activity.
  // When the requests complete, check that the new activity appears in the table.
  it("creates a new activity", () => {
    cy.intercept("POST", "http://localhost:8080/activities", (req) => {
      req.reply({
        statusCode: 200,
        body: { id: 3, name: req.body.name, type: req.body.type, participants: req.body.participants },
      });
    }).as("postNewActivity");

    cy.intercept("GET", "http://localhost:8080/activities", {
      statusCode: 200,
      body: [
        { id: 1, name: "Test Activity 1", type: "Exercise", participants: 1 },
        { id: 2, name: "Test Activity 2", type: "Study", participants: 2 },
        { id: 3, name: "New Activity", type: "Custom", participants: 4 }
      ],
    }).as("getActivitiesAfterPost");

    cy.contains("button", "Add New Activity").click();

    cy.get('[role="dialog"]').within(() => {
      cy.get("input").eq(0).clear();
      cy.get("input").eq(0).type("New Activity");
      cy.get("input").eq(1).clear();
      cy.get("input").eq(1).type("Custom");
      cy.get("input").eq(2).clear();
      cy.get("input").eq(2).type("4");

      cy.contains("button", "Save").click();
    });

    cy.wait("@postNewActivity");
    cy.wait("@getActivitiesAfterPost");

    cy.get("tbody > tr").should("have.length", 3);
    cy.get("tbody > tr").last().should("contain", "New Activity");
  });

  // Stub the PUT call to update an activity, and the subsequent call to refresh the data.
  // Open the edit dialog by clicking the edit icon in the first row.
  // Change the text in the "Name" field and save the edited activity.
  // When the requests complete, check that the new activity appears in the table.
  it("edits an existing activity", () => {
    cy.intercept("PUT", /http:\/\/localhost:8080\/activities\/\d+/, (req) => {
      req.reply({
        statusCode: 200,
        body: { id: req.url.split("/").pop(), ...req.body },
      });
    }).as("putActivity");

    cy.intercept("GET", "http://localhost:8080/activities", {
      statusCode: 200,
      body: [
        { id: 1, name: "Updated Activity", type: "Exercise", participants: 1 },
        { id: 2, name: "Test Activity 2", type: "Study", participants: 2 },
      ],
    }).as("getActivitiesAfterPut");

    cy.get('button[aria-label="edit"]').first().click();

    cy.get('[role="dialog"]').should("be.visible");

    cy.get('[role="dialog"] input').eq(0).clear();
    cy.get('[role="dialog"] input').eq(0).type("Updated Activity");

    cy.get('[role="dialog"]').contains("button", "Save").click();

    cy.wait("@putActivity");
    cy.wait("@getActivitiesAfterPut");

    cy.get("tbody > tr")
      .first()
      .should("contain", "Updated Activity");
  });

  // Stub the DELETE call to delete an activity, and the subsequent call to refresh the data.
  // Delete the first activity by clicking the delete icon in the first row.
  // When the requests complete, check that only one row remains, and that it's not the deleted activity.
  it("deletes an activity", () => {
    cy.intercept("DELETE", /http:\/\/localhost:8080\/activities\/\d+/, {
      statusCode: 200,
    }).as("deleteActivity");

    cy.intercept("GET", "http://localhost:8080/activities", {
      statusCode: 200,
      body: [
        { id: 2, name: "Test Activity 2", type: "Study", participants: 2 }
      ],
    }).as("getActivitiesAfterDelete");

    cy.get('button[aria-label="delete"]').first().click();

    cy.wait("@deleteActivity");
    cy.wait("@getActivitiesAfterDelete");

    cy.get("tbody > tr").should("have.length", 1);
    cy.get("tbody > tr").first().should("contain", "Test Activity 2");
  });

  // Stub the POST call to create a random activity, and the subsequent call to refresh the data.
  // Add a random activity by clicking on the "Add Random Activity" button.
  // When the requests complete, verify that the new activity exists in the table and has the correct name.
  it("adds a random activity", () => {
    cy.intercept("POST", "http://localhost:8080/activities/random", {
      statusCode: 200,
      body: { id: 4, name: "Random Activity", type: "Random", participants: 3 },
    }).as("postRandomActivity");

    cy.intercept("GET", "http://localhost:8080/activities", {
      statusCode: 200,
      body: [
        { id: 1, name: "Test Activity 1", type: "Exercise", participants: 1 },
        { id: 2, name: "Test Activity 2", type: "Study", participants: 2 },
        { id: 4, name: "Random Activity", type: "Random", participants: 3 }
      ],
    }).as("getActivitiesAfterRandom");

    cy.contains("button", "Add Random Activity").click();

    cy.wait("@postRandomActivity");
    cy.wait("@getActivitiesAfterRandom");

    cy.get("tbody > tr").should("have.length", 3);
    cy.get("tbody > tr").last().should("contain", "Random Activity");
  });

  describe("Activity Manager Error Handling", () => {
    it("should display an error message when fetching activities fails", () => {
      cy.intercept('GET', 'http://localhost:8080/activities', {
        statusCode: 500,
        body: {},
      }).as('getActivitiesError');

      cy.visit('/');
      cy.wait('@getActivitiesError');
      cy.contains('Error fetching activities.').should('be.visible');
    });

    it("should display an error message when creating a new activity fails", () => {
      cy.intercept("POST", "http://localhost:8080/activities", {
        statusCode: 500,
        body: {},
      }).as("postNewActivityError");

      cy.intercept("GET", "http://localhost:8080/activities", {
        statusCode: 200,
        body: [],
      });

      cy.contains("button", "Add New Activity").click();

      cy.get('[role="dialog"]').within(() => {
        cy.get("input").eq(0).clear();
        cy.get("input").eq(0).type("Fail Activity");
        cy.get("input").eq(1).clear();
        cy.get("input").eq(1).type("Type");
        cy.get("input").eq(2).clear();
        cy.get("input").eq(2).type("3");
        cy.contains("button", "Save").click();
      });

      cy.wait("@postNewActivityError");
      cy.contains("Error saving activity.").should("be.visible");
    });

    it("should display an error message when updating an activity fails", () => {
      cy.intercept("PUT", /http:\/\/localhost:8080\/activities\/\d+/, {
        statusCode: 500,
        body: {},
      }).as("putActivityError");

      cy.get('button[aria-label="edit"]').first().click();

      cy.get('[role="dialog"]').should("be.visible");

      cy.get('[role="dialog"] input').eq(0).clear();
      cy.get('[role="dialog"] input').eq(0).type("Update Failure");

      cy.get('[role="dialog"]').contains("button", "Save").click();

      cy.wait("@putActivityError");
      cy.contains("Error saving activity.").should("be.visible");
    });

    it("should display an error message when deleting an activity fails", () => {
      cy.intercept("DELETE", /http:\/\/localhost:8080\/activities\/\d+/, {
        statusCode: 500,
        body: {},
      }).as("deleteActivityError");

      cy.get('button[aria-label="delete"]').first().click();

      cy.wait("@deleteActivityError");
      cy.contains("Error deleting activity.").should("be.visible");
    });

    it("should display an error message when adding a random activity fails", () => {
      cy.intercept("POST", "http://localhost:8080/activities/random", {
        statusCode: 500,
        body: {},
      }).as("postRandomActivityError");

      cy.contains("button", "Add Random Activity").click();

      cy.wait("@postRandomActivityError");
      cy.contains("Error adding random activity.").should("be.visible");
    });
  });
});

Cypress.Commands.add('loginTestUser', () => {
    cy.fixture('settings').then(settings => {
        cy.visit(settings.baseUrl);
        cy.get('input[name="username"]').type(settings.testUser);
        cy.get('input[name="password"]').type(settings.testPw);
        cy.contains('button', 'Login').click();
    })
})

Cypress.Commands.add('sendChatMessage', (matchName, msg) => {
    cy.fixture('settings').then(settings => {
        cy.contains('a', matchName).click();
        cy.wait(300);
        cy.get('textarea[id="message"]').type(msg);
        cy.get('button[id="sendMessage"]').click();

        cy.contains(msg).should('be.visible');
    })
})

Cypress.Commands.add('chatScrollUntil', (matchName, msg) => {
    cy.fixture('settings').then(settings => {
        cy.contains('a', matchName).click();
        cy.contains(msg).should('be.visible');
    })
})

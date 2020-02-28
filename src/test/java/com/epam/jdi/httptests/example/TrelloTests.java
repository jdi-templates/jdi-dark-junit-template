package com.epam.jdi.httptests.example;

import com.epam.jdi.httptests.example.dto.Board;
import com.epam.jdi.httptests.example.dto.Card;
import com.epam.jdi.httptests.example.dto.Organization;
import com.epam.jdi.httptests.example.dto.TrelloList;
import com.epam.jdi.httptests.utils.TrelloDataGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.stream.Collectors;

import static com.epam.http.requests.ServiceInit.init;

/**
 * Example of creating business flow tests
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TrelloTests {

    @BeforeAll
    public void initService() {
        init(TrelloService.class);
    }

    @Test
    public void createCardInBoard() {

        //Crate board
        Board board = TrelloDataGenerator.generateBoard();
        Board createdBoard = TrelloService.createBoard(board);
        Board gotBoard = TrelloService.getBoard(createdBoard.getId());
        Assertions.assertEquals(gotBoard.getName(), createdBoard.getName(), "Name of created board is incorrect");

        //Create list
        TrelloList tList = TrelloDataGenerator.generateList(createdBoard);
        TrelloList createdList = TrelloService.createList(tList);

        //Create Card
        Card card = TrelloDataGenerator.generateCard(createdBoard, createdList);
        Card createdCard = TrelloService.addNewCardToBoard(card);

        //Check that card was added
        Board cardBoard = TrelloService.getCardBoard(createdCard.getId());
        Assertions.assertEquals(cardBoard.getName(), board.getName(), "Card wasn't added to board");
    }

    @Test
    public void assignBoardToOrganization() {

        //Create organization
        Organization organization = TrelloDataGenerator.generateOrganization();
        Organization createOrg = TrelloService.createOrganization(organization);

        //Crate board
        Board board = TrelloDataGenerator.generateBoard();
        board.setIdOrganization(createOrg.getId());
        TrelloService.createBoard(board);

        //Check that organization contains created board
        List<Board> boards = TrelloService.getOrganizationBoards(createOrg);
        Assertions.assertTrue(boards.stream().map(Board::getName).collect(Collectors.toList()).contains(board.getName()), "Board wasn't added to organization");

    }
}

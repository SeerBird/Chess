package game.model;

import game.model.moves.CastleMove;
import game.model.moves.Move;
import game.model.moves.PromotionMove;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static game.model.PieceType.*;
import static game.model.Position.pos;
import static game.model.moves.MoveMode.*;

public class Board {
    boolean check;
    Map<Position, Piece> board;
    KingData black;
    KingData white;
    public Move lastMove;
    ArrayList<Move> moves;

    public Board() {
        check = false;
        board = new HashMap<>();
        lastMove = new Move(4, 0, new Piece(true, king, pos(4, 0)), peaceful);
    }

    /**
     * @return An {@link ArrayList} of all the board states accessible through available moves.
     * {@code null} for draw, empty list for loss.
     */
    public ArrayList<Board> getPossibleMoves() {
        ArrayList<Board> futures = new ArrayList<>();
        //region check for draws by insufficient material
        if (board.values().size() < 5) {
            if (board.values().size() == 2) {
                return null;
            } else if (board.values().size() == 3) {
                for (Piece material : board.values()) {
                    if (material.type == knight || material.type == bishop) {
                        return null;
                    }
                }
            } else {
                ArrayList<Piece> bishops = new ArrayList<>();
                for (Piece material : board.values()) {
                    if (material.type == bishop) {
                        bishops.add(material);
                    }
                }
                if (bishops.size() == 2) {
                    Piece bishop1 = bishops.get(0);
                    Piece bishop2 = bishops.get(1);
                    if (bishop1.color != bishop2.color) {
                        if (tileColor(bishop1.pos.x, bishop1.pos.y) == tileColor(bishop2.pos.x, bishop2.pos.y)) {
                            return null;
                        }
                    }
                }
            }
        }
        //endregion
        //region create and add all the futures that the moves would create
        for (Move move : moves) {
            futures.add(makeMove(move));
        }
        //endregion
        for (Board future : new ArrayList<>(futures)) {
            //region retroactively check for check and castling path safety. remove invalid futures accordingly.
            if (future.isAttacked(!future.lastMove.actor.color, future.getKingData(future.lastMove.actor.color).king.pos)) {
                futures.remove(future);
                continue;
            }
            if (future.lastMove instanceof CastleMove) {
                int direction = (int) Math.signum(future.lastMove.dest.x - future.lastMove.actor.pos.x);
                for (int x = future.lastMove.actor.pos.x + direction; // original location being under attack would have made this impossible
                     x != future.lastMove.dest.x; // destination was just checked above
                     x += direction) {
                    if (future.isAttacked(!future.lastMove.actor.color, pos(x, future.lastMove.actor.pos.y))) {
                        futures.remove(future);
                        break;
                    }
                }
            }
            //endregion
            //region determine whether the other king is in check now
            if (future.isAttacked(!future.lastMove.actor.color, future.getKingData(!future.lastMove.actor.color).king.pos)) {
                future.check = true;
            }
            //endregion
        }
        //region return null if stalemate
        if (futures.isEmpty()) {
            if (!check) {
                return null;
            }
        }
        //endregion
        return futures;
    }

    @Nullable
    public Piece getPiece(int x, int y) {
        return board.get(pos(x, y));
    }

    public void reset() {
        board.clear();
        for (int i = 0; i < 8; i++) {
            board.put(pos(i, 1), new Piece(true, pawn, pos(i, 1)));
            board.put(pos(i, 6), new Piece(false, pawn, pos(i, 6)));
        }
        //region black non-pawns
        board.put(pos(0, 0), new Piece(true, rook, pos(0, 0)));
        board.put(pos(1, 0), new Piece(true, knight, pos(1, 0)));
        board.put(pos(2, 0), new Piece(true, bishop, pos(2, 0)));
        board.put(pos(3, 0), new Piece(true, queen, pos(3, 0)));
        board.put(pos(4, 0), new Piece(true, king, pos(4, 0)));
        board.put(pos(5, 0), new Piece(true, bishop, pos(5, 0)));
        board.put(pos(6, 0), new Piece(true, knight, pos(6, 0)));
        board.put(pos(7, 0), new Piece(true, rook, pos(7, 0)));
        //endregion
        //region white non-pawns
        board.put(pos(0, 7), new Piece(false, rook, pos(0, 7)));
        board.put(pos(1, 7), new Piece(false, knight, pos(1, 7)));
        board.put(pos(2, 7), new Piece(false, bishop, pos(2, 7)));
        board.put(pos(3, 7), new Piece(false, queen, pos(3, 7)));
        board.put(pos(4, 7), new Piece(false, king, pos(4, 7)));
        board.put(pos(5, 7), new Piece(false, bishop, pos(5, 7)));
        board.put(pos(6, 7), new Piece(false, knight, pos(6, 7)));
        board.put(pos(7, 7), new Piece(false, rook, pos(7, 7)));
        //endregion
        black = new KingData(board.get(pos(4, 0)));
        white = new KingData(board.get(pos(4, 7)));
        generatePossiblyCheckMoves();
    }

    public static boolean tileColor(int x, int y) {
        return (x % 2 == 0 ^ y % 2 == 0);
    }

    @NotNull
    private Board makeMove(@NotNull Move move) {
        Board newBoard = new Board();
        newBoard.board = new HashMap<>(board);
        newBoard.lastMove = move;
        newBoard.setKingData(true, new KingData(black, black.king));
        newBoard.setKingData(false, new KingData(white, white.king));
        if (move.actor.type == king) {
            //region move
            newBoard.board.put(move.dest, new Piece(move.actor, move.dest));
            newBoard.board.remove(move.actor.pos);
            //endregion
            KingData newData = new KingData(getKingData(move.actor.color), newBoard.board.get(move.dest));
            //region castle
            if (move instanceof CastleMove) {
                if (move.dest.x == 2) {
                    newBoard.board.remove(pos(0, move.actor.pos.y));
                    newBoard.board.put(pos(3, move.actor.pos.y), new Piece(move.actor.color, rook, pos(3, move.actor.pos.y)));
                    newData.rook0Moved = true;
                } else {//move.dest == 6
                    newBoard.board.remove(pos(7, move.actor.pos.y));
                    newBoard.board.put(pos(5, move.actor.pos.y), new Piece(move.actor.color, rook, pos(5, move.actor.pos.y)));
                    newData.rook7Moved = true;
                }
            }
            //endregion
            newData.kingMoved = true;
            newBoard.setKingData(move.actor.color, newData);
        } else if (move.actor.type == rook) {
            //region move
            newBoard.board.put(move.dest, new Piece(move.actor, move.dest));
            newBoard.board.remove(move.actor.pos);
            //endregion
            if (move.actor.pos.x == 0) { //may repeat throughout the game. I don't care.
                newBoard.getKingData(move.actor.color).rook0Moved = true;
            } else if (move.actor.pos.x == 7) {
                newBoard.getKingData(move.actor.color).rook7Moved = true;
            }
        } else if (move.actor.type == pawn) {
            if (move instanceof PromotionMove) {
                newBoard.board.remove(move.actor.pos);
                newBoard.board.put(((PromotionMove) move).promoted.pos, ((PromotionMove) move).promoted);
            } else {
                if (move.dest.x != move.actor.pos.x) {
                    if (getPiece(move.dest.x, move.dest.y) == null) { //en passant
                        newBoard.board.remove(pos(move.dest.x, move.dest.y - pawnDirection(move.actor.color))); //remove the attacked pawn
                    }
                }
                newBoard.board.put(move.dest, new Piece(move.actor, move.dest));
                newBoard.board.remove(move.actor.pos);
            }
        } else {
            //region move
            newBoard.board.put(move.dest, new Piece(move.actor, move.dest));
            newBoard.board.remove(move.actor.pos);
            //endregion
        }
        newBoard.generatePossiblyCheckMoves();
        return newBoard;
    }

    private void generatePossiblyCheckMoves() {
        moves = new ArrayList<>();
        Piece target;
        int x;
        int y;
        for (Piece actor : board.values()) {
            if (actor.color == lastMove.actor.color) {
                continue;
            }
            switch (actor.type) {
                case pawn -> {
                    x = actor.pos.x;
                    y = actor.pos.y;
                    int direction = pawnDirection(actor.color);
                    //region en passant
                    if ((target = lastMove.actor).type == pawn) {
                        if (Math.abs(target.pos.y - lastMove.dest.y) == 2) {
                            if (lastMove.dest.y == y) {
                                if (Math.abs(lastMove.dest.x - x) == 1) {
                                    moves.add(new Move(lastMove.dest.x, y + direction, actor, capture));
                                }
                            }
                        }
                    }
                    //endregion
                    //region double jump
                    if ((actor.color && y == 1) || ((!actor.color) && y == 6)) {
                        if (getPiece(x, y + direction) == null && getPiece(x, y + 2 * direction) == null) {
                            moves.add(new Move(x, y + 2 * direction, actor, peaceful));
                        }
                    }
                    //endregion
                    //region potential promotions
                    ArrayList<Move> potentialPromotions = new ArrayList<>();
                    //region capture
                    if (isFoe(actor.color, x + 1, y + direction)) {
                        potentialPromotions.add(new Move(x + 1, y + direction, actor, capture));
                    }
                    if (isFoe(actor.color, x - 1, y + direction)) {
                        potentialPromotions.add(new Move(x - 1, y + direction, actor, capture));
                    }
                    //endregion
                    //region normal move
                    if (y + direction >= 0 && y + direction < 8) {
                        if (getPiece(x, y + direction) == null) {
                            potentialPromotions.add(new Move(x, y + direction, actor, peaceful));
                        }
                    }
                    //endregion
                    //region check for promotions
                    for (Move move : potentialPromotions) {
                        if ((move.dest.y == 7 && move.actor.color) || (move.dest.y == 0 && !move.actor.color)) {
                            for (PieceType type : PieceType.values()) {
                                if (type == king || type == pawn) {
                                    continue;
                                }
                                moves.add(new PromotionMove(move.dest.x, move.dest.y, move.actor, move.mode, type));
                            }
                        } else {
                            moves.add(move);
                        }
                    }
                    //endregion
                    //endregion
                }
                case bishop -> {
                    addMoveRay(actor, 1, 1);
                    addMoveRay(actor, 1, -1);
                    addMoveRay(actor, -1, -1);
                    addMoveRay(actor, -1, 1);
                }
                case knight -> {
                    addMoveIfSimplyValid(actor, 1, 2);
                    addMoveIfSimplyValid(actor, 1, -2);
                    addMoveIfSimplyValid(actor, -1, -2);
                    addMoveIfSimplyValid(actor, -1, 2);
                    addMoveIfSimplyValid(actor, 2, 1);
                    addMoveIfSimplyValid(actor, 2, -1);
                    addMoveIfSimplyValid(actor, -2, -1);
                    addMoveIfSimplyValid(actor, -2, +1);
                }
                case queen -> {
                    addMoveRay(actor, 1, 1);
                    addMoveRay(actor, 1, -1);
                    addMoveRay(actor, -1, -1);
                    addMoveRay(actor, -1, 1);
                    addMoveRay(actor, 1, 0);
                    addMoveRay(actor, 0, -1);
                    addMoveRay(actor, -1, 0);
                    addMoveRay(actor, 0, 1);
                }
                case rook -> {
                    addMoveRay(actor, 0, 1);
                    addMoveRay(actor, 0, -1);
                    addMoveRay(actor, -1, 0);
                    addMoveRay(actor, 1, 0);
                }
                case king -> {
                    //region movement
                    addMoveIfSimplyValid(actor, 1, 1);
                    addMoveIfSimplyValid(actor, 1, -1);
                    addMoveIfSimplyValid(actor, -1, -1);
                    addMoveIfSimplyValid(actor, -1, 1);
                    addMoveIfSimplyValid(actor, 1, 0);
                    addMoveIfSimplyValid(actor, 0, -1);
                    addMoveIfSimplyValid(actor, -1, 0);
                    addMoveIfSimplyValid(actor, 0, 1);
                    //endregion
                    //region castle. region of shame.
                    if (!check) {
                        KingData data = getKingData(actor.color);
                        Piece rookig;
                        if (!data.kingMoved) {
                            if (!data.rook0Moved) {
                                if ((rookig = getPiece(0, backLineY(actor.color))) != null) {
                                    if (rookig.type == rook && rookig.color == actor.color) {
                                        if (getPiece(3, actor.pos.y) == null) {
                                            if (getPiece(2, actor.pos.y) == null) {
                                                if (getPiece(1, actor.pos.y) == null) {
                                                    moves.add(new CastleMove(2, actor.pos.y, actor));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (!data.rook7Moved) {
                                if ((rookig = getPiece(7, backLineY(actor.color))) != null) {
                                    if (rookig.type == rook && rookig.color == actor.color) {
                                        if (getPiece(5, actor.pos.y) == null) {
                                            if (getPiece(6, actor.pos.y) == null) {
                                                moves.add(new CastleMove(6, actor.pos.y, actor));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //endregion
                }
            }
        }
    }

    //region move helper methods
    private void addMoveRay(Piece actor, int dx, int dy) {
        int targetx;
        int targety;
        Piece target;
        for (int i = 1; i < 8; i++) {
            targetx = actor.pos.x + dx * i;
            targety = actor.pos.y + dy * i;
            if ((target = getPiece(targetx, targety)) == null) {
                if (targetx > 7 || targetx < 0 || targety > 7 || targety < 0) {
                    break;
                }
                moves.add(new Move(targetx, targety, actor, normal));
            } else if (target.color ^ actor.color) {
                moves.add(new Move(targetx, targety, actor, capture));
                break;
            } else {
                break;
            }
        }
    }

    private void addMoveIfSimplyValid(@NotNull Piece actor, int dx, int dy) {
        int x = actor.pos.x + dx;
        int y = actor.pos.y + dy;
        if (x > 7 || x < 0 || y > 7 || y < 0) {
            return;
        }
        Piece target;
        if ((target = getPiece(x, y)) != null) {
            if (target.color != actor.color) {
                moves.add(new Move(x, y, actor, capture));
            }
        } else {
            moves.add(new Move(x, y, actor, normal));
        }

    }

    private int pawnDirection(boolean color) {
        if (color) {
            return 1;
        }
        return -1;
    }

    private int backLineY(boolean color) {
        if (color) {
            return 0;
        }
        return 7;
    }

    private boolean isFoe(boolean color, int x, int y) {
        Piece target = getPiece(x, y);
        return target != null && target.color ^ color;
    }
    //endregion

    private boolean isAttacked(boolean attacker, Position pos) {
        boolean attacked = false;
        for (Move move : moves) {
            if ((move.mode == peaceful)) {
                continue;
            }
            if (move.actor.color == attacker) {
                attacked |= move.dest.equals(pos);
            }
        }
        //region this is only needed so that pawns stop kings from castling by attacking squares the kings need to go through
        for (Piece actor : board.values()) {
            if (actor.type == pawn) {
                if (actor.color == attacker) {
                    attacked |= (actor.pos.y + pawnDirection(attacker) == pos.y) && (Math.abs(actor.pos.x - pos.x) == 1);
                }
            }
        }
        //endregion
        return attacked;
    }

    private KingData getKingData(boolean color) {
        if (color) {
            return black;
        }
        return white;
    }

    public void setKingData(boolean color, KingData data) {
        if (color) {
            black = data;
        } else {
            white = data;
        }
    }

    public Collection<Piece> getPieces() {
        return board.values();
    }
}

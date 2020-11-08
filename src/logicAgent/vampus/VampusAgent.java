package logicAgent.vampus;

import logicAgent.vampus.rules.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VampusAgent {

    public static final int START_AGENT_ROW = VampusGame.START_AGENT_ROW;
    public static final int START_AGENT_COL = VampusGame.START_AGENT_COL;

    private int agentRow = START_AGENT_ROW;
    private int agentCol = START_AGENT_COL;

    private final int WIDTH = VampusGame.WIDTH;
    private final int HEIGHT = VampusGame.HEIGHT;

    private final List<VampusAbstractRule> rules;

    private final CellInfo[][] cellsInfo;
    private final VampusSensors[][] sensorsInfo;

    private VampusAgentMove.Type lastMoveType = null;

    public VampusAgent() {
        this.cellsInfo = initCellsInfo();
        this.sensorsInfo = new VampusSensors[HEIGHT][WIDTH];
        this.rules = List.of(
                new VampusStenchRule(cellsInfo, sensorsInfo),
                new VampusBreezeRule(cellsInfo, sensorsInfo),
                new VampusOkRule(cellsInfo, sensorsInfo),
                new VampusHereRule(cellsInfo, sensorsInfo)
        );
    }

    public CellInfo[][] initCellsInfo(){
        CellInfo[][] info = new CellInfo[HEIGHT][WIDTH];

        for(int row = 0; row < VampusGame.HEIGHT; row++){
            for(int col = 0; col < VampusGame.WIDTH; col++){
                info[row][col] = new CellInfo();
            }
        }
        return info;
    }

    public VampusAgentMove decideMove(VampusSensors vampusSensors) {
        moveBackIfBump(vampusSensors);
        concludeAll(vampusSensors);

        if (vampusSensors.isGlitter()) {
            return new VampusAgentMove(VampusAgentMove.Type.GRAB_GOLD);
        }

        List<VampusAgentMove.Type> types = new ArrayList<>();
        if (agentRow < HEIGHT - 1 && cellsInfo[agentRow + 1][agentCol].isOk()) {
            types.add(VampusAgentMove.Type.MOVE_DOWN);
        }
        if (agentRow > 0 && cellsInfo[agentRow - 1][agentCol].isOk()) {
            types.add(VampusAgentMove.Type.MOVE_UP);
        }
        if (agentCol < WIDTH - 1 && cellsInfo[agentRow][agentCol + 1].isOk()) {
            types.add(VampusAgentMove.Type.MOVE_RIGHT);
        }
        if (agentCol > 0 && cellsInfo[agentRow][agentCol - 1].isOk()) {
            types.add(VampusAgentMove.Type.MOVE_LEFT);
        }

        if (types.isEmpty()) {
            return new VampusAgentMove(VampusAgentMove.Type.FINISH);
        }

        VampusAgentMove.Type choice = types.get(new Random().nextInt(types.size()));
        move(choice);
        lastMoveType = choice;
        return new VampusAgentMove(choice);
    }

    private void concludeAll(VampusSensors vampusSensors) {
        for (VampusAbstractRule rule:rules) {
            rule.conclude(agentRow, agentCol, vampusSensors);
        }
    }

    private void moveBackIfBump(VampusSensors sensors) {
        if (sensors.isBump()) {
            switch (lastMoveType) {
                case MOVE_UP:
                    agentRow++;
                    break;
                case MOVE_DOWN:
                    agentRow--;
                    break;
                case MOVE_LEFT:
                    agentCol++;
                    break;
                case MOVE_RIGHT:
                    agentCol--;
                    break;
            }
        }
    }

    private void move(VampusAgentMove.Type type) {
            switch (type) {
                case MOVE_UP:
                    agentRow--;
                    break;
                case MOVE_DOWN:
                    agentRow++;
                    break;
                case MOVE_LEFT:
                    agentCol--;
                    break;
                case MOVE_RIGHT:
                    agentCol++;
                    break;
            }
    }

}

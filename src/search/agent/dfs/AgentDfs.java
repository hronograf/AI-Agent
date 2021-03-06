package search.agent.dfs;

import search.agent.Agent;
import search.environment.EnvironmentInterface;
import search.environment.MoveInterface;

import java.util.HashSet;
import java.util.Set;

// M - class of moves that applied to search.environment
// I - class of ID of search.environment states
public class AgentDfs<M extends MoveInterface<M, I>, I> extends Agent {

    private final EnvironmentInterface<M, I> environment;
    private final Set<I> visited;

    private Integer totalCost;

    public AgentDfs(EnvironmentInterface<M, I> environment) {
        this.environment = environment;
        this.visited = new HashSet<>();
        this.totalCost = 0;
    }

    @Override
    public void run() {
        dfs();
    }

    private boolean dfs() {
        visit();
        if (isFinish()) {
            return true;
        }
        for (M move : this.environment.getPossibleMoves()) {
            if (!visited(move.getTargetId())) {
                this.doMove(move);
                if (dfs()) {
                    return true;
                }
                this.doMove(move.getReverseMove());
            }
        }
        return false;
    }

    private void visit() {
        I currId = this.environment.getId();
        this.visited.add(currId);
    }

    private boolean isFinish () {
        return this.environment.isFinish();
    }

    private boolean visited(I id) {
        return this.visited.contains(id);
    }

    private void doMove(M move) {
        this.environment.doMove(move);
        this.totalCost += move.getCost();
    }

    @Override
    public Integer getTotalCost() {
        return this.totalCost;
    }
}

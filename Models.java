package br.com.rotalivre;

import java.util.List;
import jakarta.validation.constraints.*;

public final class Models {
    private Models() {}
    public record Node(int id, String label, int x, int y, String kind, boolean synthetic) {}
    public record Edge(int id, int fromNode, int toNode, String label, double distanceM,
        int widthCm, double slopePct, boolean stepFree, String surface, String conditionStatus,
        Long verifiedAt, String source, boolean bidirectional, boolean synthetic) {}
    public record Report(long id, int edgeId, String category, String description,
        boolean blocksPath, String status, long createdAt, long updatedAt, boolean synthetic) {}
    public record History(long id, long reportId, String previousStatus, String newStatus,
        String note, long createdAt) {}
    public record Transport(int id, int nodeId, String label, String mode, String accessibility,
        String information, String source, long updatedAt, boolean synthetic) {}
    public record Network(List<Node> nodes, List<Edge> edges, List<Integer> blockedEdges,
        String dataset, String geographicReference, List<String> warnings) {}
    public record RouteResult(boolean found, List<Node> nodes, List<Edge> edges,
        double distanceM, Double baselineDistanceM, Double detourM, Double detourPercent,
        List<Integer> excludedEdges, String message, List<String> warnings,
        String algorithm, double computationMs, boolean synthetic, long generatedAt) {}
    public record Constraints(int minWidthCm, double maxSlopePct, int maxAgeDays) {
        public Constraints {
            if (minWidthCm < 60 || minWidthCm > 200 || !Double.isFinite(maxSlopePct)
                || maxSlopePct < 0 || maxSlopePct > 20 || maxAgeDays < 1 || maxAgeDays > 365)
                throw new IllegalArgumentException("Restrições fora dos limites do piloto.");
        }
    }
    public record CreateReport(
        @Min(1) int edgeId,
        @NotNull @Pattern(regexp="OBRA|OBSTRUCAO|DEGRAU|PISO|REBAIXAMENTO|OUTRO") String category,
        @NotBlank @Size(min=5,max=500) String description,
        @NotNull Boolean blocksPath) {}
    public record UpdateReport(
        @NotNull @Pattern(regexp="CONFIRMED|REJECTED|RESOLVED") String status,
        @NotBlank @Size(min=5,max=300) String note) {}
}

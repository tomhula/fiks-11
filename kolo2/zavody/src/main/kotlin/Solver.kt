import java.util.*

class Solver(val race: Race)
{
    private val graph: MutableMap<Node, List<Node>> = mutableMapOf()
    private val sectorNodes = mutableSetOf<Node>()
    private val simplifiedGraph: MutableMap<Node, List<Edge>> = mutableMapOf()
    private lateinit var startNode: Node
    private lateinit var endNode: Node

    fun solve(): Int
    {
        buildGraph()
        buildSimplifiedGraph()
        return findBestPossibleTime()
    }

    private fun findShortestTimeDumb(startNode: Node, endNode: Node): Int
    {
        val dist = mutableMapOf<Node, NodeMeta>().withDefault { NodeMeta(Int.MAX_VALUE, race.initialStepTime) }
        val priorityQueue = PriorityQueue<Node>(compareBy { dist.getValue(it).time })

        dist[startNode] = NodeMeta(0, race.initialStepTime)
        priorityQueue.add(startNode)

        while (priorityQueue.isNotEmpty())
        {
            val currentNode = priorityQueue.poll()
            val currentMeta = dist.getValue(currentNode)
            for ((neighbor, distance) in simplifiedGraph[currentNode] ?: emptyList())
            {
                val time = currentMeta.time + currentMeta.stepTime * distance

                if (time < dist.getValue(neighbor).time)
                {
                    val newStepTime = currentMeta.stepTime + ((neighbor.sector as? Sector.Speed)?.stepTimeModifier ?: 0)
                    val newStepTimeFinal = if (newStepTime > race.maxStepTime || newStepTime < race.minStepTime)
                        currentMeta.stepTime
                    else
                        newStepTime

                    dist[neighbor] = NodeMeta(time, newStepTimeFinal)
                    priorityQueue.add(neighbor)
                }
            }
        }

        return dist[endNode]!!.time
    }

    private fun findBestPossibleTime(): Int
    {
        val dist = mutableMapOf<Node, NodeMeta>()
        dist[startNode] = NodeMeta(0, race.initialStepTime)

        val terminatorTime = findShortestTimeDumb(startNode, endNode)

        fun explore(sectorNode: Node, time: Int, stepTime: Int)
        {
            val neighbourConnections = simplifiedGraph[sectorNode] ?: return

            val newPossibleStepTime = stepTime + ((sectorNode.sector as? Sector.Speed)?.stepTimeModifier ?: 0)
            val newStepTime = if (newPossibleStepTime in race.stepTimeRange)
                newPossibleStepTime
            else
                stepTime

            for ((neighbour, distance) in neighbourConnections)
            {
                val neighbourMeta = dist[neighbour] ?: NodeMeta(Int.MAX_VALUE, Int.MAX_VALUE)
                val neighbourTime = time + newStepTime * distance
                val newPathMeta = NodeMeta(neighbourTime, newStepTime)

                if (neighbourTime > terminatorTime)
                    continue

                if (!neighbourMeta.isSurelyBetter(newPathMeta))
                {
                    dist[neighbour] = newPathMeta
                    explore(neighbour, neighbourTime, newStepTime)
                }
            }
        }

        explore(startNode, 0, race.initialStepTime)
        return dist[endNode]?.time ?: -1
    }

    private fun buildGraph()
    {
        val (newSpaceSize, newSpaceOrigin) = determineSpace()

        val newSpace = Array(newSpaceSize.x) { Array(newSpaceSize.y) { Array(newSpaceSize.z) { null as Sector? } } }

        for (sector in race.sectors)
        {
            val newSpacePos = sector.pos - newSpaceOrigin
            newSpace[newSpacePos.x][newSpacePos.y][newSpacePos.z] = sector.copy(newSpacePos)
        }

        fun IntVector.toNode(): Node?
        {
            if (this !in newSpace)
                return null

            return when (val sector = newSpace[x][y][z])
            {
                is Sector.NoGo -> null
                null -> Node(this)
                else -> Node(this, sector)
            }
        }

        for (x in 0..<newSpaceSize.x)
            for (y in 0..<newSpaceSize.y)
                for (z in 0..<newSpaceSize.z)
                {
                    val sector = newSpace[x][y][z]
                    val pos = IntVector(x, y, z)

                    val node = pos.toNode() ?: continue

                    if (node.sector != null)
                        sectorNodes.add(node)

                    if (sector is Sector.Start)
                        startNode = node
                    else if (sector is Sector.End)
                        endNode = node

                    val adjacentNodes: MutableList<Node> = LinkedList()

                    for (neighborPos in pos.neighbours)
                    {
                        val neighbourNode = neighborPos.toNode() ?: continue
                        adjacentNodes.add(neighbourNode)
                        graph[node] = adjacentNodes
                    }
                }
    }

    /**
     * Finds the shortest distance, that does not go through any sectors from the [startSectorNode] to every other sector.
     * Also includes a distance to [startSectorNode] itself (which is 2 if it exists).
     */
    private fun findShortestNonSectorDistanceToAllSectors(startSectorNode: Node): Map<Node, Int>
    {
        check(startSectorNode.sector != null) { "The start node must be a sector node" }

        val dist = mutableMapOf<Node, Int>().withDefault { Int.MAX_VALUE }
        val resultDist = mutableMapOf<Node, Int>().withDefault { Int.MAX_VALUE }
        val priorityQueue = PriorityQueue<Node>(compareBy { dist.getValue(it) })

        dist[startSectorNode] = 0

        val startNodeNeighbours = graph[startSectorNode] ?: emptyList()
        if (startNodeNeighbours.any { it.sector == null })
            resultDist[startSectorNode] = 2

        priorityQueue.add(startSectorNode)

        while (priorityQueue.isNotEmpty())
        {
            val currentNode = priorityQueue.poll()
            val currentNodeDist = dist.getValue(currentNode)

            val neighbours = graph[currentNode] ?: emptyList()
            for (neighbor in neighbours)
            {
                val newDist = currentNodeDist + 1
                val oldDist = dist.getValue(neighbor)
                if (newDist < oldDist)
                {
                    dist[neighbor] = newDist

                    if (neighbor.sector != null)
                        resultDist[neighbor] = newDist
                    else
                        priorityQueue.add(neighbor)
                }
            }
        }

        return resultDist
    }

    private fun buildSimplifiedGraph()
    {
        for (sectorNode in sectorNodes)
        {
            val nonSectorDistances = findShortestNonSectorDistanceToAllSectors(sectorNode)
            val edges = nonSectorDistances.map { (node, distance) -> Edge(node, distance) }
            simplifiedGraph[sectorNode] = edges
        }
    }

    private fun determineSpace(): Pair<IntVector, IntVector>
    {
        var minX = race.size.x
        var maxX = -1
        var minY = race.size.y
        var maxY = -1
        var minZ = race.size.z
        var maxZ = -1

        for (sector in race.sectors)
        {
            val pos = sector.pos
            /* Only NoGo sectors have padding, if we did not,
            we couldn't go around it. (which would originally be possible */
            val padding = if (sector is Sector.NoGo) 1 else 0
            minX = minOf(minX, pos.x - padding)
            maxX = maxOf(maxX, pos.x + padding)
            minY = minOf(minY, pos.y - padding)
            maxY = maxOf(maxY, pos.y + padding)
            minZ = minOf(minZ, pos.z - padding)
            maxZ = maxOf(maxZ, pos.z + padding)
        }

        minX = maxOf(minX, 0)
        maxX = minOf(maxX, race.size.x - 1)
        minY = maxOf(minY, 0)
        maxY = minOf(maxY, race.size.y - 1)
        minZ = maxOf(minZ, 0)
        maxZ = minOf(maxZ, race.size.z - 1)

        val newSize = IntVector(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1)
        /* The new space's origin place in the old space */
        val newSpaceOrigin = IntVector(minX, minY, minZ)
        return Pair(newSize, newSpaceOrigin)
    }
}

private data class Edge(val node: Node, val distance: Int)

private data class NodeMeta(val time: Int, val stepTime: Int)
{
    fun isSurelyBetter(other: NodeMeta) =
        this.time < other.time && this.stepTime < other.stepTime
                || this.time == other.time && this.stepTime < other.stepTime
                || this.stepTime == other.stepTime && this.time < other.time
}

private data class Node(val pos: IntVector, val sector: Sector? = null)
{
    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other !is Node) return false

        return pos == other.pos && sector == other.sector
    }

    override fun hashCode() = pos.hashCode()
}
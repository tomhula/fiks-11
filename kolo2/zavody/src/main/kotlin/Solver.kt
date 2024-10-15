import java.util.*

class Solver(val race: Race)
{
    private val graph: MutableMap<Node, List<Node>> = mutableMapOf()
    private lateinit var startNode: Node
    private lateinit var endNode: Node

    fun solve(): Int
    {
        buildGraph()
        return algo()
    }

    private fun algo(): Int
    {
        val dist = mutableMapOf<Node, NodeMeta>().withDefault { NodeMeta(Int.MAX_VALUE, race.initialStepTime) }
        val prev = mutableMapOf<Node, Node?>()
        val priorityQueue = PriorityQueue<Node>(compareBy { dist.getValue(it).timeToGetTo })

        dist[startNode] = NodeMeta(0, race.initialStepTime)
        priorityQueue.add(startNode)

        while (priorityQueue.isNotEmpty())
        {
            val currentNode = priorityQueue.poll()

            //if (currentNode == endNode) break

            val currentMeta = dist.getValue(currentNode)

            for (neighbor in graph[currentNode] ?: emptyList())
            {
                val timeToGetTo = currentMeta.timeToGetTo + currentMeta.stepTime
                if (timeToGetTo < dist.getValue(neighbor).timeToGetTo)
                {
                    val newStepTime = currentMeta.stepTime + (neighbor.speedSector?.stepTimeModifier ?: 0)
                    val newStepTimeFinal = if (newStepTime > race.maxStepTime || newStepTime < race.minStepTime)
                        currentMeta.stepTime
                    else
                        newStepTime
                    dist[neighbor] = NodeMeta(timeToGetTo, newStepTimeFinal)
                    prev[neighbor] = currentNode
                    priorityQueue.add(neighbor)
                }
            }
        }

        // Reconstruct the shortest path
        val path = LinkedList<Node>()
        var pathNode: Node? = endNode
        while (pathNode != null)
        {
            path.addFirst(pathNode)
            pathNode = prev[pathNode]
        }

        // Print the shortest path
        /*println("Shortest path: ${path.joinToString(" -> ") { it.pos.toString() }}")
        println("Shortest distance: ${dist[endNode]}")*/

        return dist[endNode]!!.timeToGetTo
    }

    private fun buildGraph()
    {
        fun IntVector.toNode(): Node?
        {
            if (this !in race)
                return null

            return when (val sector = race.sectors[x][y][z])
            {
                is Sector.Speed -> Node(this, sector)
                is Sector.NoGo -> null
                else -> Node(this)
            }
        }

        for (x in 0..<race.size.x)
            for (y in 0..<race.size.y)
                for (z in 0..<race.size.z)
                {
                    val sector = race.sectors[x][y][z]
                    val pos = IntVector(x, y, z)

                    val node = pos.toNode() ?: continue

                    if (sector is Sector.Start)
                        startNode = node
                    else if (sector is Sector.End)
                        endNode = node

                    val adjacentNodes: MutableList<Node> = LinkedList()

                    for (neighborPos in pos.neighbours)
                    {
                        val neighbourNode = neighborPos.toNode() ?: continue
                        if (::startNode.isInitialized && neighbourNode.pos == startNode.pos)
                            continue
                        adjacentNodes.add(neighbourNode)
                        graph[node] = adjacentNodes
                    }
                }
    }

    private fun printGraph()
    {
        graph.forEach { (node, neighbours) ->
            println("${node.pos} -> ${neighbours.joinToString { it.pos.toString() }}")
        }
    }

    private data class NodeMeta(val timeToGetTo: Int, val stepTime: Int)

    private data class Node(val pos: IntVector, val speedSector: Sector.Speed? = null)
    {
        constructor(sector: Sector) : this(sector.pos)

        override fun equals(other: Any?): Boolean
        {
            if (this === other) return true
            if (other !is Node) return false

            if (pos != other.pos) return false
            if (speedSector != other.speedSector) return false

            return true
        }

        override fun hashCode() = pos.hashCode()
    }
}
package de.hsos.nearbychat.service.bluetooth.util

import java.util.LinkedList
import java.util.Queue

class SlidingWindowTable(private val windowSize: Int = 10) {
    private val slidingWindows: HashMap<String, SlidingWindow> = HashMap()

    fun add(address: String, id:Char): Boolean{
        if(this.slidingWindows[address] == null){
            this.slidingWindows[address] = SlidingWindow()
        }
        return this.slidingWindows[address]!!.add(id)
    }

    inner class SlidingWindow {
        private var oldestID: Char = '0'
        private val idGenerator: AtomicIdGenerator = AtomicIdGenerator()
        private val queue: Queue<Char> = LinkedList()

        @Synchronized
        fun add(id: Char): Boolean {
            return if (this.queue.isEmpty() && AtomicIdGenerator.idIsValid(id)) {
                this.oldestID = id - this@SlidingWindowTable.windowSize / 2
                this.idGenerator.setID(this.oldestID)
                this.queue.add(id)
                true
            } else {
                if (AtomicIdGenerator.idIsValid(id)
                    && !this.queue.contains(id)
                    && SlidingWindowTable.isInRange(this.oldestID, id, this@SlidingWindowTable.windowSize)
                ) {
                    this.queue.add(id)
                    this.oldestID = this.idGenerator.next()
                    while (this.queue.peek() != null && SlidingWindowTable.isInRange(
                            this.queue.peek() as Char,
                            this.oldestID,
                            this@SlidingWindowTable.windowSize
                        )
                    ) {
                        this.queue.poll()
                    }
                    true
                } else {
                    false
                }
            }
        }
        fun getOldestID() :Char = this.oldestID
    }


    fun getOldestID(address: String): Char? = this.slidingWindows[address]?.getOldestID()

    companion object {
        private fun isInRange(first: Char, second: Char, range: Int): Boolean {
            val idGenerator: AtomicIdGenerator = AtomicIdGenerator(first)
            for (i in 1..range) {
                if (idGenerator.next() == second) {
                    return true
                }
            }
            return false
        }
    }

}

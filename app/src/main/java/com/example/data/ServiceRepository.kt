package com.example.data

import kotlinx.coroutines.flow.Flow

class ServiceRepository(private val dao: ServiceDao) {
    val allTechnicians: Flow<List<Technician>> = dao.getAllTechnicians()
    val allPayments: Flow<List<PaymentRecord>> = dao.getAllPayments()

    suspend fun getTechnicianById(id: Long): Technician? {
        return dao.getTechnicianById(id)
    }

    suspend fun insertTechnician(technician: Technician): Long {
        return dao.insertTechnician(technician)
    }

    suspend fun deleteTechnician(technician: Technician) {
        dao.deleteTechnician(technician)
    }

    suspend fun ensureDefaultData() {
        if (dao.getTechnicianCount() == 0) {
            val initialTechs = listOf(
                Technician(
                    name = "পারভেজ ইলেকট্রিক এন্ড স্যানিটারি সার্ভিস",
                    category = "ইলেকট্রিক ও স্যানিটারি",
                    phone = "01700000000",
                    address = "মেঘনা, কুমিল্লা",
                    rating = 4.9,
                    completedJobs = 34
                ),
                Technician(
                    name = "রফিকুল ইসলাম",
                    category = "সিসি ক্যামেরা সেটআপ",
                    phone = "01800000000",
                    address = "মেঘনা, কুমিল্লা",
                    rating = 4.8,
                    completedJobs = 21
                ),
                Technician(
                    name = "আরিফ হোসেন",
                    category = "সোলার প্যানেল ও আইপিএস",
                    phone = "01900000000",
                    address = "মেঘনা, কুমিল্লা",
                    rating = 4.7,
                    completedJobs = 19
                ),
                Technician(
                    name = "মো: শাহিন আলম",
                    category = "এসি ও ফ্রিজ মেরামত",
                    phone = "01711223344",
                    address = "মানিকারচর বাজার, মেঘনা, কুমিল্লা",
                    rating = 4.9,
                    completedJobs = 28
                ),
                Technician(
                    name = "কামাল হোসেন",
                    category = "হোম পেইন্টিং ও ডেকোরেশন",
                    phone = "01822334455",
                    address = "মেঘনা ঘাট, কুমিল্লা",
                    rating = 4.6,
                    completedJobs = 15
                )
            )
            dao.insertAllTechnicians(initialTechs)
        }
    }

    fun getMessagesForTechnician(techId: Long): Flow<List<ChatMessage>> {
        return dao.getMessagesForTechnician(techId)
    }

    suspend fun insertMessage(message: ChatMessage): Long {
        return dao.insertMessage(message)
    }

    fun getPaymentsForTechnician(techId: Long): Flow<List<PaymentRecord>> {
        return dao.getPaymentsForTechnician(techId)
    }

    suspend fun insertPayment(payment: PaymentRecord): Long {
        return dao.insertPayment(payment)
    }
}

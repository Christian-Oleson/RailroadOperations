package com.oleson.services

import com.oleson.models.ClassificationDbModel
import com.oleson.models.dto.ClassificationType
import com.oleson.repositories.ClassificationRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Singleton
class ClassificationService(
    private val classificationRepository: ClassificationRepository
) {
    private val logger = LoggerFactory.getLogger(ClassificationService::class.java)

    fun createClassificationTable() : Flux<ClassificationType> {
        logger.info("Creating Classification Table")
        return classificationRepository.createClassificationTable().mapNotNull { mapClassification(it) }
    }

    fun createClassification(id: Int, name: String) : Mono<ClassificationDbModel> {
        val classification = ClassificationType.fromInt(id)

        if (classification == null) {
            val classificationDbModel = ClassificationDbModel()
            classificationDbModel.pk = "classification#${id}"
            classificationDbModel.sk = name

           return classificationRepository.createClassification(classificationDbModel)
        }

        return Mono.just(classification)
            .mapNotNull { it }
            .flatMap {
                val classificationDbModel = ClassificationDbModel()
                classificationDbModel.pk = "Classification#${it.ordinal}"
                classificationDbModel.sk = it.name
                classificationRepository.createClassification(classificationDbModel)
            }
    }

    fun deleteClassification(id: Int) : Mono<Unit> {
        return Mono.just(classificationRepository.deleteClassification(id))
    }

    fun getClassification(id: Int) : Mono<ClassificationDbModel> {
        return classificationRepository.getClassification(id)
    }

    private fun mapClassification(classification: ClassificationDbModel) : ClassificationType? {
        return classification.pk?.let {
            ClassificationType.fromInt(it.split("classification#").last().toInt())
        }
    }

    fun getClassifications() : Mono<List<ClassificationDbModel>> {
        return classificationRepository.getAllClassifications()
            .flatMap { list ->
            Mono.just(list.filter { item -> item.pk?.startsWith("classification#") == true }) // Filter the list and create a new Mono
        }
    }
}
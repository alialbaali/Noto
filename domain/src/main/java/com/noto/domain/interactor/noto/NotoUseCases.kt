package com.noto.domain.interactor.noto


class NotoUseCases(
    val createNoto: CreateNoto,
    val updateNoto: UpdateNoto,
    val deleteNoto: DeleteNoto,
    val getNoto: GetNoto,
    val getNotos: GetNotos,
    val getNotoLabels: GetNotoLabels,
    val insertNotoLabels: InsertNotoLabels,
    val updateNotoLabels: UpdateNotoLabels
)
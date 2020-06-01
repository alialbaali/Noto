package com.noto.domain.interactor.noto


class NotoUseCases(
    val createNoto: CreateNoto,
    val updateNoto: UpdateNoto,
    val deleteNoto: DeleteNoto,
    val getNoto: GetNoto,
    val getNotos: GetNotos
)
package com.noto.domain.interactor.noto


class NotoUseCases(
    val createNoto: CreateNoto,
    val updateNoto: UpdateNoto,
    val deleteNoto: DeleteNoto,
    val getNotoById: GetNoto,
    val getNotos: GetNotos,
    val getAllNotos: GetAllNotos,
    val getArchivedNotos: GetArchivedNotos,
    val countLibraryNotos: CountLibraryNotos
)
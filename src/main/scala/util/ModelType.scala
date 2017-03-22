package eldis.redux.rrf.util

import eldis.redux.rrf.{ ModelLens, StringLens }
import eldis.redux.rrf.raw.impl.{ Model => RawModel }

/**
 * Magnet for global and partial lenses
 */
// TODO: global and partial lenses have differing domains
case class ModelType[A, B](
  value: Either[ModelLens[A, B], ModelLens.Partial[A, B]]
)

object ModelType {
  implicit def modelLensIsModelType[A, B](ml: ModelLens[A, B]): ModelType[A, B] =
    ModelType[A, B](Left(ml))
  implicit def partialModelLensIsModelType[A, B](pl: ModelLens.Partial[A, B]): ModelType[A, B] =
    ModelType[A, B](Right(pl))
  implicit def stringLensIsModelType[A, B](sl: StringLens[A, B]): ModelType[A, B] =
    ModelType[A, B](Left(sl))
  implicit def partialStringLensIsModelType[A, B](pl: StringLens.Partial[A, B]): ModelType[A, B] =
    ModelType[A, B](Right(pl))

  def run[A, B](t: ModelType[A, B]): RawModel =
    t.value.fold(ModelLens.toRawModel, ModelLens.Partial.toRawModel)
}

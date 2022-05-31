package com.friendsfinder.app.service.modification;


import com.friendsfinder.app.model.Node;

import java.util.ArrayList;

/**
 * Алгоритм изменения ширины и глубины графа
 *
 * n - глубина
 * w - ширина
 * tuples - список нод
 * blocks - все tuples на уровне / w
 *
 * Если добавляем w (new w > old w)
 * 1. Сначала добавить в существующих tuples новые ноды
 * 2. Добавить внутри blocks (tuples.size / w) новые tuples
 * 3. Добавить новые tuples справа от остальных
 *
 * Если удаляем w (new w < old w)
 * 1. Удалить tuples справа
 * 2. Удалить tuples внутри blocks
 * 3. Удалить ноды внутри tuples
 *
 * Все это повторяем от 0 до n (идем в глубину графа)
 *
 * Если w увеличилось, а n уменьшилось,
 * то сначала обрезаем нижние уровни, а потом меняем остальное
 *
 * Если w уменьшилось, а n увеличилось,
 * то сначала меняем текущие ноды, а потом добавляем снизу новые уровни
 *
 * Нужно возвращать два графа: один идет в БД, второй на клиент
 */
public interface IModificationService {
}

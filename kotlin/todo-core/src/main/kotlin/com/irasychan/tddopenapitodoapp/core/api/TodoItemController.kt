package com.irasychan.tddopenapitodoapp.core.api

import com.irasychan.tddopenapitodoapp.core.api.model.NewTodoItem
import com.irasychan.tddopenapitodoapp.core.domain.TodoItem
import com.irasychan.tddopenapitodoapp.core.domain.TodoItemRepository
import com.irasychan.tddopenapitodoapp.core.domain.TodoItemStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.*
import java.util.logging.Logger
import com.irasychan.tddopenapitodoapp.core.api.model.TodoItemStatus as TodoItemStatusDTO
import com.irasychan.tddopenapitodoapp.core.api.model.TodoItem as TodoItemDTO

@RestController
class TodoItemController(
    private val todoItemRepository: TodoItemRepository
) : TodosApi {
    // logger
    private val logger: Logger = Logger.getLogger(TodoItemController::class.java.name)

    override fun createTodo(newTodoItem: NewTodoItem): ResponseEntity<TodoItemDTO> {
        logger.info("Creating a new todo item")

        // validate the new TodoItem again as openapi generator does not validate empty string
        // will likely fix in the later version, or we can use a different generator
        if (newTodoItem.name.isBlank()) {
            // TODO: implement a better response for bad request
            return ResponseEntity.badRequest().build()
        }

        // TODO: move to application layer as a service class
        val newTodo = TodoItem(
            id = UUID.randomUUID(),
            name = newTodoItem.name,
            description = newTodoItem.description,
            status = TodoItemStatus.valueOf(newTodoItem.status.toString()),
            dueDate = newTodoItem.dueDate
        )
        todoItemRepository.save(newTodo)

        return ResponseEntity.created(
            URI("/todos/${newTodo.id}")
        ).body(newTodo.toDto())
    }

    override fun getAllTodos(): ResponseEntity<List<TodoItemDTO>> {
        logger.info("Getting all todo items")
        return ResponseEntity.ok(
            todoItemRepository.findAll().map { it.toDto() }
        )
    }

    override fun getTodoById(id: UUID): ResponseEntity<TodoItemDTO> {
        logger.info("Getting a todo item with: id=${id}")

        // TODO: turn into a controller advice for resource not found case
        val todoItem = todoItemRepository.findById(id).orElse(null) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(todoItem.toDto())
    }

    override fun updateTodo(id: UUID, todoItem: TodoItemDTO): ResponseEntity<TodoItemDTO> {
        logger.info("Updating a todo item with: id=${id}")

        val existingTodo = todoItemRepository.findById(id).orElse(null) ?: return ResponseEntity.notFound().build()

        val updatedTodo = existingTodo.copy(
            name = todoItem.name ?: existingTodo.name,
            description = todoItem.description,
            status = TodoItemStatus.valueOf(todoItem.status.toString()),
            dueDate = todoItem.dueDate
        )

        return ResponseEntity.ok(todoItemRepository.save(updatedTodo).toDto())
    }

    override fun deleteTodo(id: UUID): ResponseEntity<Unit> {
        logger.info("Deleting a todo item with: id=${id}")

        val todoItem = todoItemRepository.findById(id).orElse(null) ?: return ResponseEntity.notFound().build()
        todoItemRepository.delete(todoItem)

        return ResponseEntity.noContent().build()
    }
}

private fun TodoItem.toDto(): TodoItemDTO {
    return TodoItemDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        status = TodoItemStatusDTO.valueOf(this.status.toString()),
        dueDate = this.dueDate
    )
}

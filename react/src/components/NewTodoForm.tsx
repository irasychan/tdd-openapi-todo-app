import React, { useState } from 'react'
import { useTodoContext } from '../contexts/TodoContext'

const NewTodoForm = (): JSX.Element => {
  const [todoDescription, setTodoDescription] = useState('')
  const [errorMessage, setErrorMessage] = useState('')

  const { addTodo } = useTodoContext()

  const handleAddTodo = (e: React.FormEvent<HTMLFormElement>): void => {
    e.preventDefault()
    if (todoDescription === '') {
      setErrorMessage('Description is required')
      return
    }
    addTodo(todoDescription)
    setTodoDescription('')
  }

  return (
    <form
      data-testid="newTodoForm"
      onSubmit={handleAddTodo}
    >
      <div>
        <label htmlFor="todoDescription">Description</label>
        <input
          type="text"
          id="todoDescription"
          data-testid="newTodoDescription"
          value={todoDescription}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => setTodoDescription(e.target.value)}
        />
      </div>
      <button type="submit" data-testid="newTodoButton">
        Add Todo
      </button>
      {(errorMessage !== '') && <span>{errorMessage}</span>}
    </form>
  )
}

export default NewTodoForm

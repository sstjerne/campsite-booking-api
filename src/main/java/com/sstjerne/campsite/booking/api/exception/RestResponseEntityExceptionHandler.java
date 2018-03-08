package com.sstjerne.campsite.booking.api.exception;

import java.sql.SQLException;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import com.sstjerne.campsite.booking.api.model.ResponseMessage;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {
	
	
	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private MessageSource msgSource;

	@ExceptionHandler(value = { MethodArgumentNotValidException.class })
	public ResponseEntity<Object> processValidationError(MethodArgumentNotValidException ex, final WebRequest request) {
		BindingResult result = ex.getBindingResult();
		FieldError error = result.getFieldError();

		ResponseMessage errorMessage = null;
		if (error != null) {
			Locale currentLocale = LocaleContextHolder.getLocale();
			String msg = msgSource.getMessage(error.getDefaultMessage(), null, currentLocale);
			errorMessage = new ResponseMessage();
			errorMessage.setMessage(msg);
			errorMessage.setReason(HttpStatus.BAD_REQUEST.getReasonPhrase());
			errorMessage.setCode(HttpStatus.BAD_REQUEST.value());
		}
		
		return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

	}
	
	@ExceptionHandler(value = { CustomMethodArgumentNotValidException.class })
	public ResponseEntity<Object> processCustomValidationError(CustomMethodArgumentNotValidException ex, final WebRequest request) {

		Locale currentLocale = LocaleContextHolder.getLocale();
		String msg = msgSource.getMessage(ex.getMessage(), null, currentLocale);
		ResponseMessage errorMessage = new ResponseMessage();
		errorMessage.setMessage(msg);
		errorMessage.setReason(HttpStatus.BAD_REQUEST.getReasonPhrase());
		errorMessage.setCode(HttpStatus.BAD_REQUEST.value());
		
		return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

	}
	

	@ExceptionHandler({ RepositoryConstraintViolationException.class , ConstraintViolationException.class, DataIntegrityViolationException.class })
	public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
		String message = "This should be application specific";
		if (ex.getCause() != null && ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
			message = "Resource has an error or this resource already exists";
		}
		if (ex.getCause() != null && ex.getCause() instanceof org.springframework.data.rest.core.RepositoryConstraintViolationException) {
			RepositoryConstraintViolationException nevEx = (RepositoryConstraintViolationException) ex;
			String errors = nevEx.getErrors().getAllErrors().stream().map(p -> p.toString()).collect(Collectors.joining("\n"));
			message = errors.isEmpty() ? "Resource has an error or this resource already exists" : "Resource has the following errors : " + errors;
		}
		
		ResponseMessage errorMessage = new ResponseMessage();
		errorMessage.setMessage(message);
		errorMessage.setReason(HttpStatus.BAD_REQUEST.getReasonPhrase());
		errorMessage.setCode(HttpStatus.BAD_REQUEST.value());
		return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { EntityNotFoundException.class })
	protected ResponseEntity<Object> handleNotFound(final RuntimeException ex, final WebRequest request) {
		final String message = String.format("Resource %s not found and/or don't have permissions to do this operation", ex.getMessage() != null ? ex.getMessage() : "");
		
		ResponseMessage errorMessage = new ResponseMessage();
		errorMessage.setMessage(message);
		errorMessage.setReason(HttpStatus.NOT_FOUND.getReasonPhrase());
		errorMessage.setCode(HttpStatus.NOT_FOUND.value());
		return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class })
	protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
		ResponseMessage errorMessage = new ResponseMessage();
		errorMessage.setMessage("Resource has a conflict");
		errorMessage.setReason(HttpStatus.CONFLICT.getReasonPhrase());
		errorMessage.setCode(HttpStatus.CONFLICT.value());
		return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class, SQLException.class })
	public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
		logger.error("500 Status Code", ex);
		ResponseMessage errorMessage = new ResponseMessage();
		errorMessage.setMessage("Please try later or contact us");
		errorMessage.setReason(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		errorMessage.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
	
	/**
	 * A single place to customize the response body of all Exception types.
	 * <p>The default implementation sets the {@link WebUtils#ERROR_EXCEPTION_ATTRIBUTE}
	 * request attribute and creates a {@link ResponseEntity} from the given
	 * body, headers, and status.
	 * @param ex the exception
	 * @param body the body for the response
	 * @param headers the headers for the response
	 * @param status the response status
	 * @param request the current request
	 */
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		}
		return new ResponseEntity<Object>(body, headers, status);
	}

}
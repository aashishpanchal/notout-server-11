package com.choic11.service;

import com.choic11.model.TblState;
import com.choic11.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StateService {
	@Autowired
	private StateRepository stateRepository;

	public List<TblState> listAllState(int countryId) {
		return stateRepository.getStates(countryId);
	}
	public List<TblState> listAllStatea() {
		return stateRepository.getStates(1);
	}

}

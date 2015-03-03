package de.hshannover.f4.trust.visitmeta.ifmap.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import de.hshannover.f4.trust.ifmapj.messages.ResultItem;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;

public class SearchResultMock implements IfmapMock<SearchResult> {

	private SearchResult mSearchResult_mock;

	private List<ResultItem> mResultItems;

	public SearchResultMock(SearchResult.Type type) {
		mSearchResult_mock = mock(SearchResult.class);
		mResultItems = new ArrayList<ResultItem>();

		when(mSearchResult_mock.getResultItems()).thenReturn(mResultItems);
		when(mSearchResult_mock.getType()).thenReturn(type);
	}

	public SearchResultMock(List<ResultItem> resultItems, SearchResult.Type type) {
		this(type);

		mResultItems = resultItems;
		when(mSearchResult_mock.getResultItems()).thenReturn(mResultItems);
	}

	public void addResultItem(ResultItem resultItem) {
		mResultItems.add(resultItem);
		when(mSearchResult_mock.getResultItems()).thenReturn(mResultItems);
	}

	@Override
	public SearchResult getMock() {
		return mSearchResult_mock;
	}

}

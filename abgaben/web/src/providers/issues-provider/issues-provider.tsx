import React, { useReducer, useContext, useMemo, useCallback } from 'react';
import { Issue } from '../../typings/provider-data-interfaces';
import { issuesReducer, IssuesState } from './issues-reducer';
import Logger from '../../services/logger/logger';
import {
  fetchIssuesRequest,
  fetchIssuesSuccess,
  fetchIssuesFailure,
  addIssueRequest,
  addIssueFailure,
  updateIssueRequest,
  updateIssueFailure,
  addIssueSuccess,
  updateIssueSuccess
} from './issues-actions';
import { issues } from '../../services/ad-api';
import {
  mapIssueDtoToIssues,
  mapInternalIssueToIssueDTO
} from '../../lib/mappers';

interface IssuesContext {
  issues: Issue[] | null;
  isLoading: boolean;
  error: Error | null;
  fetchIssues: () => Promise<boolean>;
  addIssue: (issue: Partial<Issue>) => Promise<boolean>;
  updateIssue: (id: string, update: Partial<Issue>) => Promise<boolean>;
}

const initialContext: IssuesState = {
  issues: null,
  isLoading: false,
  error: null
};

const IssuesContext = React.createContext<IssuesContext | undefined>(undefined);

interface IssuesProviderProps {
  override?: Partial<IssuesContext>;
}

export const IssuesProvider: React.FC<IssuesProviderProps> = ({
  override,
  children
}) => {
  const [state, dispatch] = useReducer(issuesReducer, initialContext);

  const fetchIssues = useCallback(async () => {
    try {
      Logger.logBreadcrumb('info', 'issues-context', 'Fetching issues');
      dispatch(fetchIssuesRequest());

      const res = await issues.getAllIssues();
      Logger.logBreadcrumb('info', 'issues-context', 'Fetched issues');
      dispatch(fetchIssuesSuccess(res.map(r => mapIssueDtoToIssues(r))));
      return true;
    } catch (e) {
      Logger.logBreadcrumb('error', 'issues-context', 'Fetch issues failed');
      Logger.captureException(e);
      dispatch(fetchIssuesFailure(e));
      return false;
    }
  }, []);

  const addIssue = useCallback(async (issue: Partial<Issue>) => {
    try {
      Logger.logBreadcrumb('info', 'issues-context', 'Adding issue');
      dispatch(addIssueRequest());

      const res = await issues.createNewIssue(
        mapInternalIssueToIssueDTO(issue)
      );
      Logger.logBreadcrumb('info', 'issues-context', 'Added issue');
      dispatch(addIssueSuccess(mapIssueDtoToIssues(res)));
      return true;
    } catch (e) {
      Logger.logBreadcrumb('error', 'issues-context', 'Add issue failed');
      Logger.captureException(e);
      dispatch(addIssueFailure(e));
      return false;
    }
  }, []);

  const updateIssue = useCallback(
    async (id: string, update: Partial<Issue>) => {
      try {
        Logger.logBreadcrumb('info', 'issues-context', 'Updating issue');
        dispatch(updateIssueRequest(id));

        const res = await issues.updateIssue(
          id,
          mapInternalIssueToIssueDTO(update)
        );
        Logger.logBreadcrumb('info', 'issues-context', 'Updated issue');
        dispatch(updateIssueSuccess(mapIssueDtoToIssues(res)));
        return true;
      } catch (e) {
        Logger.logBreadcrumb('error', 'issues-context', 'Update issue failed');
        Logger.captureException(e);
        dispatch(updateIssueFailure(id, e));
        return false;
      }
    },
    []
  );

  // Override the internal state with a possible override for test purposes
  const context = useMemo(
    () => ({ ...state, fetchIssues, addIssue, updateIssue, ...override }),
    [state, fetchIssues, addIssue, updateIssue, override]
  );

  return (
    <IssuesContext.Provider value={context}>{children}</IssuesContext.Provider>
  );
};

interface IssuesKit {
  issues: Issue[] | null;
  isLoading: boolean;
  error: Error | null;
  addIssue: (meter: Partial<Issue>) => Promise<boolean>;
}

let fetching = false;
export function useIssues(): IssuesKit {
  const context = useContext(IssuesContext);

  if (typeof context === 'undefined')
    throw new Error('useIssues must be used within a IssuesProvider');

  const { fetchIssues, updateIssue, ...rest } = context;

  React.useEffect(() => {
    if (fetching || rest.isLoading || rest.error || rest.issues !== null)
      return;

    fetching = true;
    fetchIssues().finally(() => (fetching = false));
  }, [fetchIssues, rest]);

  return rest;
}

interface IssueKit {
  issue: Issue;
  updateIssue: (update: Partial<Issue>) => Promise<boolean>;
}

export function useIssue(id: string): IssueKit | null {
  const context = useContext(IssuesContext);

  if (typeof context === 'undefined')
    throw new Error('useIssue must be used within a IssuesProvider');

  const { issues, updateIssue } = context;

  const issue = useMemo(
    () => (!issues ? undefined : issues.find(issue => issue.id === id)),
    [id, issues]
  );

  if (typeof issue === 'undefined') return null;

  return { issue, updateIssue: updateIssue.bind(undefined, issue.id) };
}

export function useCreateIssue(): (issue: Partial<Issue>) => Promise<boolean> {
  const context = useContext(IssuesContext);

  if (typeof context === 'undefined')
    throw new Error('useIssue must be used within a IssuesProvider');

  const { addIssue } = context;

  return addIssue;
}
